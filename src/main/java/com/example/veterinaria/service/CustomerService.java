package com.example.veterinaria.service;

import com.example.veterinaria.entity.Customer;
import com.example.veterinaria.entity.Pet;
import com.example.veterinaria.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public void updateCustomer(Customer customer, Long id) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if(optionalCustomer.isPresent()){
            // como no puedo setearle a un optional, guardo en una variable ya del tipo del objeto Customer para setearle los valores.
            Customer existingCustomer = optionalCustomer.get();
            if(customer.getName() !=null && !customer.getName().isEmpty()) existingCustomer.setName(customer.getName());
            if(customer.getLastName() !=null && !customer.getLastName().isEmpty()) existingCustomer.setLastName(customer.getLastName());
            if(customer.getAddress() !=null && !customer.getAddress().isEmpty()) existingCustomer.setAddress(customer.getAddress());
            if(customer.getEmail() !=null && !customer.getEmail().isEmpty()) existingCustomer.setEmail(customer.getEmail());
            if(customer.getContactNumber() !=null && !customer.getContactNumber().equals("")) existingCustomer.setContactNumber(customer.getContactNumber());
            customerRepository.save(existingCustomer);
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

    // add any additional methods here

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

    public List<Customer> getCustomerByPetsId(Long id){
        return customerRepository.getCustomersByPetsId(id);
    }
}

