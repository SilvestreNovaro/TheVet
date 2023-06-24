package com.example.veterinaria.service;

import com.example.veterinaria.DTO.CustomerDTO;
import com.example.veterinaria.entity.Customer;
import com.example.veterinaria.entity.Pet;
import com.example.veterinaria.entity.Product;
import com.example.veterinaria.entity.Role;
import com.example.veterinaria.exception.NotFoundException;
import com.example.veterinaria.repository.CustomerRepository;
import com.example.veterinaria.repository.PetRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.apache.logging.log4j.util.Strings.isNotBlank;

@AllArgsConstructor
@Service

public class CustomerService {

    @Autowired
    private final CustomerRepository customerRepository;

    private final PetService petService;

    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final PetRepository petRepository;



    // YA NO SE USA.
    public Customer createCustomer(Customer customer) {

        Customer customer1 = new Customer();
        customer1.setName(customer.getName());
        customer1.setLastName(customer.getLastName());
        customer1.setAddress(customer.getAddress());
        customer1.setEmail(customer.getEmail());
        customer1.setContactNumber(customer.getContactNumber());
        customer1.setPets(customer.getPets());
        customer1.setRole(customer.getRole());

        String encodedPassword = this.passwordEncoder.encode(customer.getPassword());
        customer1.setPassword(encodedPassword);

        return customerRepository.save(customer1);
    }

    public Customer createCustomerDTO(CustomerDTO customerDTO) {

        Customer customer1 = new Customer();

        customer1.setName(customerDTO.getName());
        customer1.setLastName(customerDTO.getLastName());
        customer1.setAddress(customerDTO.getAddress());
        customer1.setEmail(customerDTO.getEmail());
        customer1.setContactNumber(customerDTO.getContactNumber());
        customer1.setPets(customerDTO.getPets());

        Long role = customerDTO.getRole_id();
        Optional<Role> roleOptional = roleService.findById(role);
        roleOptional.ifPresent(customer1::setRole);


        String encodedPassword = this.passwordEncoder.encode(customerDTO.getPassword());
        customer1.setPassword(encodedPassword);

        return customerRepository.save(customer1);
    }



    /*public void updateCustomerDTO(CustomerDTO customerDTO, Long id) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        System.out.println("optionalCustomer = " + optionalCustomer);
        if (optionalCustomer.isPresent()) {
            Customer existingCustomer = optionalCustomer.get();
            if (customerDTO.getName() != null && !customerDTO.getName().isEmpty())
                existingCustomer.setName(customerDTO.getName());
            if (customerDTO.getLastName() != null && !customerDTO.getLastName().isEmpty())
                existingCustomer.setLastName(customerDTO.getLastName());
            if (customerDTO.getAddress() != null && !customerDTO.getAddress().isEmpty())
                existingCustomer.setAddress(customerDTO.getAddress());
            if (customerDTO.getContactNumber() != null && !customerDTO.getContactNumber().equals(""))
                existingCustomer.setContactNumber(customerDTO.getContactNumber());
            if (customerDTO.getEmail() != null && !customerDTO.getEmail().isEmpty())
                existingCustomer.setEmail(customerDTO.getEmail());
            if (customerDTO.getPets() != null && !customerDTO.getPets().isEmpty())
                existingCustomer.setPets(customerDTO.getPets());
            if (customerDTO.getRole_id() != null && !customerDTO.getRole_id().equals("")) {
                Optional<Role> optionalRole = roleService.findById(customerDTO.getRole_id());
                if (optionalRole.isPresent()) {
                    Role role = optionalRole.get();
                    existingCustomer.setRole(role);
                }
            }
                if (customerDTO.getPassword() != null && !customerDTO.getPassword().isEmpty()) {
                    String encodedPassword = this.passwordEncoder.encode(customerDTO.getPassword());
                    existingCustomer.setPassword(encodedPassword);
                }

                customerRepository.save(existingCustomer);
            }

        }

     */



    public void updateCustomerDTO(CustomerDTO customerDTO, Long id) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if (optionalCustomer.isPresent()) {
            Customer existingCustomer = optionalCustomer.get();

            ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
            modelMapper.map(customerDTO, existingCustomer);

            if (isNotBlank(customerDTO.getPassword())) {
                String encodedPassword = this.passwordEncoder.encode(customerDTO.getPassword());
                existingCustomer.setPassword(encodedPassword);
            }

            customerRepository.save(existingCustomer);
        }
    }





    /*public void updateCustomer(Long id, Customer customer){
        Customer customer1 = customerRepository.findById(id).get();
        if(customer.getName()!=null && !customer.getName().isEmpty()) customer1.setName(customer.getName());
        if(customer.getLastName() != null && !customer.getLastName().isEmpty()) customer1.setLastName(customer.getLastName());
        if(customer.getAddress() != null && !customer.getAddress().isEmpty()) customer1.setAddress(customer.getAddress());
        if(customer.getEmail() != null && !customer.getEmail().isEmpty()) customer1.setEmail(customer.getEmail());
        if(customer.getContactNumber() != null && !customer.getContactNumber().equals("")) customer1.setContactNumber(customer1.getContactNumber());
        if(customer.getPassword() != null && !customer.getPassword().isEmpty()){
            String encodedPassword = this.passwordEncoder.encode(customer.getPassword());
            customer1.setPassword(encodedPassword);
        }
        customerRepository.save(customer1);
      }

     */

    // QUEDA IRRELEVANTE, SOLO ME DA LA POSIBILIDAD DE NO PASAR EL ID DEL ROL COMO PARAMETRO A ACTUALIZAR.
    public void updateCustomer(Long id, Customer customer) {
        Customer customer1 = customerRepository.findById(id).orElse(null);
        if (customer1 != null) {
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration().setPropertyCondition(ctx -> ctx.getSource() != null && !ctx.getSource().equals(""));
            modelMapper.map(customer, customer1);

            if (customer.getPassword() != null && !customer.getPassword().isEmpty()) {
                String encodedPassword = this.passwordEncoder.encode(customer.getPassword());
                customer1.setPassword(encodedPassword);
            }

            customerRepository.save(customer1);
        }
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


    public List<Pet> findCustomersPets(Long Petid) {
        List<Pet> petList = new ArrayList<>();
         List<Customer> customerList = customerRepository.findAll();
        for (Customer customer : customerList) {
            var petList1 = customer.getPets();
            for (Pet pet : petList) {
                if (pet.getId().equals(Petid))
                   petList.add(pet);
            }
        }
        return petList;
    }

    public Optional<Customer> findByEmail(String email){
        return customerRepository.findByEmail(email);
    }

    public Optional<Customer> findById(Long id){
        return customerRepository.findById(id);
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

    public List<Customer> findCustomersByPetName(String petName) {
        return customerRepository.findCustomersByPetName(petName);
    }

    // otros métodos del servicio de Customer

    public void addPetToCustomer(Long customerId, Pet pet) {
        Customer customer = customerRepository.findById(customerId).get();
        customer.getPets().add(pet);
        customerRepository.save(customer);
    }

    public void addPetToCustomer(Long customerId, Long petId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));
        Pet pet = petService.getPetById(petId).orElseThrow(() -> new RuntimeException("Pet not found"));
        Pet newPet = new Pet();
        newPet.setId(pet.getId());
        newPet.setPetName(pet.getPetName());
        newPet.setAge(pet.getAge());
        newPet.setGender(pet.getGender());
        newPet.setPetSpecies(pet.getPetSpecies());
        customer.getPets().add(newPet);
        customerRepository.save(customer);
    }


    public void addAnimalToCustomer(Long customerId, Pet pet){
        Customer customer = customerRepository.findById(customerId).get();
        customer.getPets().add(pet);
        customerRepository.save(customer);
    }

    public void addMultiplePetsToCustomer(Long customerId, List<Pet> pets){
        Customer customer = customerRepository.findById(customerId).get();
        List<Pet> petList = customer.getPets();
        List<Pet> petList1 = new ArrayList<>();
        for(Pet pet: pets){
            if(pets.size()>0){
                petList1.add(pet);
                petList.addAll(petList1);
                customer.setPets(petList);
            }
        }
        customerRepository.save(customer);

    }

    public void addRoleToCustomer(Long customerId, Role role){
        Customer customer = customerRepository.findById(customerId).get();
        //if(customer.getRole() == null || customer.getRole().equals(""))
        customer.setRole(role);
        customerRepository.save(customer);
    }




    public void deletePetsById(Long customerId, List<Long> petIds){
        Customer customer = customerRepository.findById(customerId).get();
        List<Pet> pets = customer.getPets();
        System.out.println("customer.getPets() = " + pets);
        List<Pet> petsToRemove = new ArrayList<>();
        for(Long petId : petIds){
            System.out.println("petId = " + petId);
            System.out.println("petIds = " + petIds);

            // La variable pet se sobreescribe en cada vuelta, guardando el objeto Pet que contenga el id proporcionado en petIds.
            Pet pet = pets.stream().filter(p-> p.getId().equals(petId)).findFirst().orElse(null);
            System.out.println("pet = " + pet);
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
                throw new NotFoundException("Customer not found with id: " + customerId);
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
                throw new NotFoundException("Customer not found with the id " + customerId);
            }


        }


    }







