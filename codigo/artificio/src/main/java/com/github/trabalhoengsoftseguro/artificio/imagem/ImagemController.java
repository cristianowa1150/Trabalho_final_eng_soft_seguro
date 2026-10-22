package com.github.trabalhoengsoftseguro.artificio.imagem;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Endpoint público para renderização dos arquivos de imagem (BLOB) armazenados no H2.
 */
@Controller
@RequestMapping("/imagens")
public class ImagemController {

    private final ImagemService imagemService;

    public ImagemController(ImagemService imagemService) {
        this.imagemService = imagemService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> exibirImagem(@PathVariable String id) {
        try {
            ImagemBlob blob = imagemService.recuperar(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(blob.getContentType()));
            headers.setContentLength(blob.getDados().length);
            headers.setCacheControl("max-age=86400, public");
            headers.add("Content-Security-Policy", "default-src 'none'; style-src 'unsafe-inline'; sandbox");
            headers.add("X-Content-Type-Options", "nosniff");
            headers.setContentDisposition(ContentDisposition.inline().filename("imagem").build());
            return new ResponseEntity<>(blob.getDados(), headers, HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
