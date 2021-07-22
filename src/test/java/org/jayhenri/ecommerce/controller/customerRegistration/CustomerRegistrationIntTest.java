package org.jayhenri.ecommerce.controller.customerRegistration;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.jayhenri.ecommerce.controller.CustomerRegistrationController;
import org.jayhenri.ecommerce.model.Address;
import org.jayhenri.ecommerce.model.Customer;
import org.jayhenri.ecommerce.service.AddressService;
import org.jayhenri.ecommerce.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(CustomerRegistrationController.class)
public class CustomerRegistrationIntTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private AddressService addressService;

    @MockBean
    private CustomerService customerService;

    private Customer customer;

    private CustomerRegistrationController testMe;
    
    @BeforeEach
    void setUp() {
        customer = new Customer();
        testMe = new CustomerRegistrationController(addressService, customerService);

        customer = new Customer(
                "testMe",
                "TestMe",
                "2934811932",
                "testMe@gmail.com",
                "testMePassword",
                "082395",
                new Address(
                        "Test Me",
                        "29L",
                        "0L",
                        "New York",
                        "T2K9R3",
                        "Province"
                ),
                null,
                null,
                null
        );
    }

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }  

    @Test
    void register() throws Exception {
        given(customerService.existsByPhoneNumber(customer.getPhoneNumber())).willReturn(false);
        given(addressService.isValidPostalCode(customer.getAddress().getPostalCode())).willReturn(true);

        mockMvc.perform(post("/api/register/customer")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(customer)))
            .andExpect(status().isOk());
    }

    @Test
    void registerThrowsCustomerAlreadyExistsException() throws Exception {
        given(customerService.existsByPhoneNumber(customer.getPhoneNumber())).willReturn(true);
        given(addressService.isValidPostalCode(customer.getAddress().getPostalCode())).willReturn(true);

        mockMvc.perform(post("/api/register/customer")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(customer)))
            .andExpect(status().isOk());
    }

    @Test
    void registerThrowsInvalidPostalCodeException() throws Exception {
        given(customerService.existsByPhoneNumber(customer.getPhoneNumber())).willReturn(false);
        given(customerService.existsByEmail(customer.getPhoneNumber())).willReturn(false);
        given(addressService.isValidPostalCode(customer.getAddress().getPostalCode())).willReturn(false);

        mockMvc.perform(post("/api/register/customer")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(customer)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void registerThrowsInvalidCustomerException() throws Exception {
        given(customerService.existsByPhoneNumber(customer.getPhoneNumber())).willReturn(false);
        given(customerService.existsByEmail(customer.getPhoneNumber())).willReturn(false);
        given(addressService.isValidPostalCode(customer.getAddress().getPostalCode())).willReturn(false);

        mockMvc.perform(post("/api/register/customer")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(customer)))
            .andExpect(status().isBadRequest());
    }
}
