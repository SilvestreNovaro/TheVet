package com.example.veterinaria.config;

import com.example.veterinaria.DTO.CustomerDTO;
import com.example.veterinaria.entity.Customer;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(CustomerDTO.class, Customer.class)
                .addMappings(mapping -> mapping.skip(Customer::setRole));

        // Configuración para el mapeo de IDs
        modelMapper.getConfiguration().setPropertyCondition(ctx ->
                ctx.getSource() != null && !ctx.getSource().equals(""));

        return modelMapper;
    }}
