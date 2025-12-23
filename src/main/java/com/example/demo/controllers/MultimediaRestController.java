package com.example.demo.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.models.entity.Multimedia;
import com.example.demo.models.service.IMultimedioService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

@RestController
@RequestMapping("/api")
public class MultimediaRestController {

    @Autowired
    private IMultimedioService multimedioService;


    @GetMapping("/multimedias")
    public List<Multimedia> index() {
        return multimedioService.findAll();
    }

    // ===============================
    // OBTENER POR ID
    // ===============================
    @GetMapping("/multimedias/{id}")
    public Multimedia show(@PathVariable Long id) {
        return multimedioService.findById(id);
    }

    // ===============================
    // CREAR
    // ===============================
    @PostMapping(value = "/multimedias", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Multimedia create(
            @RequestParam("imagen") MultipartFile imagen,
            @RequestParam("titulo") String titulo,
            @RequestParam("descripcion") String descripcion
    ) throws IOException {

        validarImagen(imagen);

        Path imgDir = Paths.get("uploads/imagenes");
        Files.createDirectories(imgDir);

        String imgName = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
        Path imgPath = imgDir.resolve(imgName);

        Files.copy(imagen.getInputStream(), imgPath, StandardCopyOption.REPLACE_EXISTING);

        Multimedia multimedia = new Multimedia();
        multimedia.setImagenes("uploads/imagenes/" + imgName);
        multimedia.setTitulo(titulo);
        multimedia.setDescripcion(descripcion);

        return multimedioService.save(multimedia);
    }

    // ===============================
    // EDITAR
    // ===============================
    @PutMapping(value = "/multimedias/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Multimedia update(
            @PathVariable Long id,
            @RequestParam(required = false) MultipartFile imagen,
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String descripcion
    ) throws IOException {

        Multimedia multimedia = multimedioService.findById(id);

        if (imagen != null && !imagen.isEmpty()) {
            validarImagen(imagen);

            Path imgDir = Paths.get("uploads/imagenes");
            Files.createDirectories(imgDir);

            String imgName = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
            Path imgPath = imgDir.resolve(imgName);

            Files.copy(imagen.getInputStream(), imgPath, StandardCopyOption.REPLACE_EXISTING);
            multimedia.setImagenes("uploads/imagenes/" + imgName);
        }

        if (titulo != null) {
            multimedia.setTitulo(titulo);
        }

        if (descripcion != null) {
            multimedia.setDescripcion(descripcion);
        }

        return multimedioService.save(multimedia);
    }

    // ===============================
    // ELIMINAR
    // ===============================
    @DeleteMapping("/multimedias/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws IOException {

        Multimedia multimedia = multimedioService.findById(id);

        if (multimedia.getImagenes() != null) {
            Files.deleteIfExists(Paths.get(multimedia.getImagenes()));
        }

        multimedioService.delete(id);
    }

    // ===============================
    // SERVIR IMÁGENES (MISMO CONTROLLER)
    // ===============================
    @GetMapping("/media/**")
    public ResponseEntity<Resource> verArchivo(HttpServletRequest request) throws IOException {

        String ruta = request.getRequestURI().replace("/api/media/", "");
        Path path = Paths.get(ruta).normalize();

        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }


    private void validarImagen(MultipartFile imagen) {

        if (imagen.isEmpty()) {
            throw new RuntimeException("La imagen está vacía");
        }

        String contentType = imagen.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("El archivo no es una imagen válida");
        }

        String nombre = imagen.getOriginalFilename().toLowerCase();
        if (!nombre.matches(".*\\.(png|jpg|jpeg|webp|gif)$")) {
            throw new RuntimeException("Formato de imagen no permitido");
        }
    }
}
