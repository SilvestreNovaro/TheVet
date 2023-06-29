package com.example.veterinaria.service;


import com.example.veterinaria.entity.Image;
import com.example.veterinaria.repository.ImageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service

public class ImageService {

    private final ImageRepository imageRepository;


    public List<Image> findAll(){
        return imageRepository.findAll();
    }

    public void updateImage(Image image, Long id){
        Optional<Image> imageOptional = imageRepository.findById(id);
        if(imageOptional.isPresent()){
            Image existingImage = imageOptional.get();
            if(image.getUrl()!=null && !image.getUrl().isEmpty()) existingImage.setUrl(image.getUrl());
            imageRepository.save(existingImage);
        }
    }

    public void deleteImage(Long id){
        imageRepository.deleteById(id);
    }

    public Optional<Image> findByUrl(String url){
        return imageRepository.findByUrl(url);
    }

    public Optional<Image> findById (Long id){
        return imageRepository.findById(id);
    }




    public Image saveImage(Image image) {
        return imageRepository.save(image);
    }
}
