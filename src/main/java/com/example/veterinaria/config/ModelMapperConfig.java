package com.example.veterinaria.config;


import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        // Configuración para el mapeo de IDs
        modelMapper.getConfiguration().setPropertyCondition(ctx ->
                ctx.getSource() != null && !ctx.getSource().equals(""));
        return modelMapper;
    }}
