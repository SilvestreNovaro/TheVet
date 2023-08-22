package com.example.veterinaria.service;

import com.example.veterinaria.DTO.CustomerDTO;
import com.example.veterinaria.convert.UtilityServiceCustomerPet;
import com.example.veterinaria.entity.Customer;
import com.example.veterinaria.entity.Pet;
import com.example.veterinaria.entity.Role;
import com.example.veterinaria.exception.BadRequestException;
import com.example.veterinaria.exception.NotFoundException;
import com.example.veterinaria.repository.CustomerRepository;
import com.example.veterinaria.repository.PetRepository;
import lombok.AllArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service

public class CustomerService {

    @Autowired
    private final ModelMapper modelMapper;
    private final CustomerRepository customerRepository;

    private final PetService petService;

    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final PetRepository petRepository;

    private final MailService mailService;

    private static final String NOT_FOUND_CUSTOMER = "Customer not found";

    private static final String NOT_FOUND_ROLE = "Role not found";

    @Autowired
    UtilityServiceCustomerPet utilityService;



    public void createCustomer(CustomerDTO customerDTO) {
        customerRepository.findByEmail(customerDTO.getEmail()).ifPresent( c -> {
            throw new BadRequestException("Email already in use");
        });
        Customer customer = utilityService.convertCustomerDTOtoCustomerCreate(customerDTO);
        String encodedPassword = this.passwordEncoder.encode(customerDTO.getPassword());
        customer.setPassword(encodedPassword);
        Role role = roleService.findById(3L).orElseThrow(() -> new NotFoundException(NOT_FOUND_ROLE));
        customer.setRole(role);
        mailService.sendRegistrationEmail(customer);
        customerRepository.save(customer);
    }



    public void updateCustomerDTO(CustomerDTO customerDTO, Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_CUSTOMER));
        customerRepository.findByEmail(customerDTO.getEmail()).ifPresent( c -> {
            throw new BadRequestException("Email already in use");
        });
        customer =  utilityService.convertCustomerDTOtoCustomerUpdate(customerDTO, customer);
        List<Pet> existingPets = customer.getPets();
        for (Pet pet : customerDTO.getPets()) {
            if (pet.getId() != null) {
                Pet existingPet = existingPets.stream()
                        .filter(pet1 -> pet1.getId().equals(pet.getId()))
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException("Pet not found"));
                modelMapper.map(pet, existingPet);
            }
            else{
                utilityService.createPet(customer, pet);
            }
        }
        String encodedPassword = this.passwordEncoder.encode(customerDTO.getPassword());
        customer.setPassword(encodedPassword);
        Role role = roleService.findById(customerDTO.getRoleId())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ROLE));
        customer.setRole(role);
        customerRepository.save(customer);
    }


    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    public Optional<Customer> findByName(String name){
        return customerRepository.findByName(name);
    }

    public Optional<Customer> findByEmail(String email){
        return customerRepository.findByEmail(email);
    }

    public Optional<Customer> findById(Long id){
        return customerRepository.findById(id);
    }

    public List<Customer> findAllAsc(){
        return customerRepository.findAllAsc();
    }

    public Long countCustomers(){
        return customerRepository.countCustomers();
    }


    public Long countCustomersByPetSpecies(String petSpecies) {
        return customerRepository.countCustomersByPetSpecies(petSpecies);
    }

    public List<Customer> listOfCustomersByPetSpecies(String petSpecies){
        return customerRepository.findTheCustomersByPetSpecies(petSpecies);
    }

    public List<Customer> listOfCustomersRole(){
        return customerRepository.findAllCustomerByRole();
    }

    public List<Customer> listCustomersWithSeniorPets(){
        return customerRepository.findOldPets();
    }

    public List<Pet> findPetsByCustomerName(String name) {
        return customerRepository.findPetsByCustomerName(name);
    }

    public List<Pet> findPetsByCustomerLastName(String lastName){
        return customerRepository.findPetsByCustomerLastName(lastName);
    }

    public Optional<Customer> findByLastName(String lastName){
        return customerRepository.findByLastName(lastName);
    }

    public Optional<Customer> findByLastNameAndAnddress(String lastName, String address){
        return customerRepository.findByLastNameAndAddress(lastName, address);
    }

    public List<Customer> findCustomersByPetName(String petName) {
        return customerRepository.findCustomersByPetName(petName);
    }

    public void addAnimalToCustomer(Long customerId, Pet pet){
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new NotFoundException(NOT_FOUND_CUSTOMER));
        customer.getPets().add(pet);
        customerRepository.save(customer);
    }

    public void addMultiplePetsToCustomer(Long customerId, List<Pet> pets){
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new NotFoundException(NOT_FOUND_CUSTOMER));
        List<Pet> petList = customer.getPets();
        for(Pet pet: pets){
            petList.add(pet);
            customer.setPets(petList);
        }
        customerRepository.save(customer);
    }

    public void addRoleToCustomer(Long customerId, Long roleId){
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new NotFoundException(NOT_FOUND_CUSTOMER));
        roleService.findById(roleId).ifPresentOrElse(
                foundRole -> {
                    customer.setRole(foundRole);
                    customerRepository.save(customer);
                },
                () -> {
                    throw new NotFoundException(NOT_FOUND_ROLE);
                }
        );
        customerRepository.save(customer);
    }


    public void deletePetsById(Long customerId, List<Long> petIds){
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new NotFoundException(NOT_FOUND_CUSTOMER));
        List<Pet> pets = customer.getPets();
        List<Pet> petsToRemove = new ArrayList<>();
        for(Long petId : petIds){

            // La variable pet se sobreescribe en cada vuelta, guardando el objeto Pet que contenga el id proporcionado en petIds.
            Pet pet = pets.stream().filter(p-> p.getId().equals(petId)).findFirst().orElse(null);
            if(pet !=null){
               petsToRemove.add(pet);

            }
            pets.removeAll(petsToRemove);
            customer.setPets(pets);

        }

        customerRepository.save(customer);
    }




        public void deletePetById(Long customerId, Long petId) {
            Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
            if (optionalCustomer.isPresent()) {
                Customer customer = optionalCustomer.get();
                List<Pet> pets = customer.getPets();
                Optional<Pet> optionalPet = pets.stream().filter(p -> p.getId().equals(petId)).findFirst();
                if (optionalPet.isPresent()) {
                    Pet pet = optionalPet.get();
                    pets.remove(pet);
                    customer.setPets(pets);
                    customerRepository.save(customer);
                } else {
                    throw new NotFoundException("Pet not found with id: " + petId);
                }
            } else {
                throw new NotFoundException(NOT_FOUND_CUSTOMER);
            }
        }

    public List<Customer> findCustomerByRoleId(Long id){
        return customerRepository.findCustomerByRoleId(id);
    }


    public void deleteRoleById(Long customerId, Long roleId){
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if(optionalCustomer.isPresent()){
            Customer customer = optionalCustomer.get();
            if(customer.getRole() !=null && customer.getRole().getId().equals(roleId)){
                customer.setRole(null);
                customerRepository.save(customer);
                //roleService.delete(roleId); Si agrego esta linea, borra el rol del customer y el rol de la base de datos.
            }else {
                throw new NotFoundException("Role not found with the id " + roleId);
            }
            }else {
                throw new NotFoundException(NOT_FOUND_CUSTOMER);
            }


        }


    }







