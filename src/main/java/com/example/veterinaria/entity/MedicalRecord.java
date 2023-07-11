package com.example.veterinaria.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Getter
@Setter
@Entity
@Table(name="MedicalRecord")
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRecord {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String vaccinationStatus;
    @NotNull
    private LocalDateTime vaccineDates;
    @NotNull
    private String medication;
    @NotNull
    private Boolean isNeutered;
    @NotNull
    private String allergies;
    @NotNull
    private String existingPathologies;
    @NotNull
    private String surgeries;
    @NotNull
    private LocalDateTime recordDate;


    private String vaccinesJson;


    @JsonIgnoreProperties({"name", "email", "license" })
   @ManyToOne(cascade = CascadeType.DETACH)
   @JoinColumn(name = "vet_id")
   private Vet vet;


    public Map<String, LocalDateTime> getVaccinesMap() {
        try {
            return jsonToMap(vaccinesJson);
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>(); // Devuelve un mapa vacío en caso de error.
        }
    }


    public void setVaccinesMap(Map<String, LocalDateTime> vaccinesMap) {
        this.vaccinesJson = mapToJson(vaccinesMap);
    }

    private String mapToJson(Map<String, LocalDateTime> map) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, LocalDateTime> jsonToMap(String json) {
        if (json != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(json, new TypeReference<Map<String, LocalDateTime>>() {});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new HashMap<>(); // Devuelve un mapa vacío si el JSON es nulo o si hay un error en la conversión.
    }



}
