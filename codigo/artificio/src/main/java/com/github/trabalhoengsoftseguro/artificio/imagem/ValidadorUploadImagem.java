package com.github.trabalhoengsoftseguro.artificio.imagem;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Validador de segurança para uploads de imagens.
 * Aplica limites de tamanho, whitelist de tipos MIME, verificação de magic bytes
 * e higienização estrita de arquivos SVG contra ataques de Stored XSS.
 */
@Component
public class ValidadorUploadImagem {

    public static final int TAMANHO_MAXIMO_BYTES = 5 * 1024 * 1024; // 5 MB

    private static final byte[] MAGIC_PNG = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final byte[] MAGIC_JPEG = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] MAGIC_GIF87A = "GIF87a".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] MAGIC_GIF89A = "GIF89a".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] MAGIC_RIFF = "RIFF".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] MAGIC_WEBP = "WEBP".getBytes(StandardCharsets.US_ASCII);

    private static final Pattern TAGS_PERIGOSAS = Pattern.compile(
            "<\\s*(script|foreignobject|iframe|object|embed)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern ATRIBUTOS_EVENTO = Pattern.compile(
            "\\bon[a-z]{3,15}\\s*=", Pattern.CASE_INSENSITIVE);

    public String validarEObterMimeType(byte[] conteudo, String contentType) {
        validarTamanho(conteudo);
        String mimeNormalizado = normalizarEValidarMimeType(contentType);
        validarConteudo(conteudo, mimeNormalizado);
        return mimeNormalizado;
    }

    private void validarTamanho(byte[] conteudo) {
        if (conteudo == null || conteudo.length == 0) {
            throw new IllegalArgumentException("O arquivo de imagem não pode estar vazio");
        }
        if (conteudo.length > TAMANHO_MAXIMO_BYTES) {
            throw new IllegalArgumentException("O arquivo de imagem excede o tamanho máximo permitido de 5MB");
        }
    }

    private String normalizarEValidarMimeType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("O tipo de mídia (Content-Type) é obrigatório");
        }

        String mime = contentType.trim().toLowerCase(Locale.ROOT);
        if ("image/jpg".equals(mime)) {
            mime = "image/jpeg";
        }

        if (!isMimePermitido(mime)) {
            throw new IllegalArgumentException("Tipo de arquivo não suportado: " + contentType +
                    ". Formatos aceitos: PNG, JPEG, GIF, WEBP e SVG.");
        }
        return mime;
    }

    private boolean isMimePermitido(String mime) {
        return switch (mime) {
            case "image/png", "image/jpeg", "image/gif", "image/webp", "image/svg+xml" -> true;
            default -> false;
        };
    }

    private void validarConteudo(byte[] conteudo, String mimeType) {
        if ("image/svg+xml".equals(mimeType)) {
            validarSvg(conteudo);
        } else {
            validarAssinaturaBinaria(conteudo, mimeType);
        }
    }

    private void validarAssinaturaBinaria(byte[] conteudo, String mimeType) {
        boolean valida = switch (mimeType) {
            case "image/png" -> iniciaCom(conteudo, MAGIC_PNG);
            case "image/jpeg" -> iniciaCom(conteudo, MAGIC_JPEG);
            case "image/gif" -> iniciaCom(conteudo, MAGIC_GIF87A) || iniciaCom(conteudo, MAGIC_GIF89A);
            case "image/webp" -> isWebpValido(conteudo);
            default -> false;
        };

        if (!valida) {
            throw new IllegalArgumentException("O conteúdo do arquivo não corresponde a uma imagem válida do tipo " + mimeType);
        }
    }

    private boolean isWebpValido(byte[] conteudo) {
        if (conteudo.length < 12 || !iniciaCom(conteudo, MAGIC_RIFF)) {
            return false;
        }
        byte[] subtipo = Arrays.copyOfRange(conteudo, 8, 12);
        return Arrays.equals(subtipo, MAGIC_WEBP);
    }

    private boolean iniciaCom(byte[] dados, byte[] prefixo) {
        if (dados.length < prefixo.length) {
            return false;
        }
        for (int i = 0; i < prefixo.length; i++) {
            if (dados[i] != prefixo[i]) {
                return false;
            }
        }
        return true;
    }

    private void validarSvg(byte[] conteudo) {
        String svg = new String(conteudo, StandardCharsets.UTF_8).toLowerCase(Locale.ROOT);

        if (!svg.contains("<svg")) {
            throw new IllegalArgumentException("Arquivo SVG inválido: tag <svg> ausente");
        }
        if (TAGS_PERIGOSAS.matcher(svg).find()) {
            throw new IllegalArgumentException("O arquivo SVG contém tags não permitidas (<script>, <foreignObject>, <object>, <iframe>, <embed>)");
        }
        if (ATRIBUTOS_EVENTO.matcher(svg).find()) {
            throw new IllegalArgumentException("O arquivo SVG contém atributos de evento JavaScript não permitidos");
        }
        if (svg.contains("javascript:") || svg.contains("data:text/html")) {
            throw new IllegalArgumentException("O arquivo SVG contém URIs ou esquemas JavaScript potencialmente maliciosos");
        }
    }
}
