package com.github.trabalhoengsoftseguro.artificio.imagem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImagemBlobRepository extends JpaRepository<ImagemBlob, String> {
}
