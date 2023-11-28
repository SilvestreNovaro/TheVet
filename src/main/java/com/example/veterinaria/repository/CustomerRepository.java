package com.example.veterinaria.repository;

import com.example.veterinaria.DTO.CustomerWithNeuteredPetDTO;
import com.example.veterinaria.entity.Customer;
import com.example.veterinaria.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT p FROM Customer c JOIN c.pets p WHERE c.lastName = :lastName")
    List<Pet> findPetsByCustomerLastName(@Param("lastName") String lastName);

    @Query("SELECT c FROM Customer c WHERE c.lastName = :lastName")
    List<Customer> findByLastName(@Param("lastName")String lastName);

    @Query("SELECT c FROM Customer c JOIN c.pets p WHERE p.petName = :petName")
    List<Customer> findCustomersByPetName(@Param("petName") String petName);

    @Query("SELECT c FROM Customer c WHERE c.lastName = :lastName and c.address = :address")
    Optional<Customer> findByLastNameAndAddress(@Param("lastName") String lastName, @Param("address") String address);

    @Query("SELECT c FROM Customer c ORDER BY c.lastName ASC")
    List<Customer> findAllAsc();

    @Query("SELECT COUNT(*) c FROM Customer c")
    Long countCustomers();

    @Query("SELECT COUNT(*) FROM Customer c JOIN c.pets p WHERE p.petSpecies = :petSpecies")
    Long countCustomersByPetSpecies(@Param("petSpecies") String petSpecies);

    @Query("SELECT c FROM Customer c JOIN c.pets p WHERE p.age > 10")
    List<Customer> findOldPets();

    @Query("SELECT c FROM Customer c JOIN c.pets p WHERE p.petSpecies = :petSpecies")
    List<Customer> findTheCustomersByPetSpecies(@Param("petSpecies") String petSpecies);

    @Query("SELECT c FROM Customer c WHERE c.role.roleName = 'Customer'")
    List<Customer> findAllCustomerByRole();

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByContactNumber(String contactNumber);

    List<Customer> findCustomerByRoleId(Long idRol);


    @Query("SELECT new com.example.veterinaria.DTO.CustomerWithNeuteredPetDTO(c.id, c.name, c.address, c.email, c.contactNumber, p.id, p.petName) FROM Customer c JOIN c.pets p JOIN p.medicalRecords mr WHERE mr.isNeutered = true")
    List<CustomerWithNeuteredPetDTO> findAllNeuteredAnimals();





}
