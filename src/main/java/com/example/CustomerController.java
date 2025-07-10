package com.example;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.userdetails.UserDetails;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;

    public CustomerController(CustomerRepository customerRepository, AddressRepository addressRepository) {
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
    }

    private void addUserNameToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof OAuth2User) {
                OAuth2User oauth2User = (OAuth2User) principal;
                model.addAttribute("userName", oauth2User.getAttribute("name"));
            } else if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                model.addAttribute("userName", userDetails.getUsername());
            } else {
                model.addAttribute("userName", principal.toString());
            }
        }
    }

    @GetMapping("/{id}")
    public String customerDetail(@PathVariable Long id, Model model) {
        addUserNameToModel(model);
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer != null) {
            customer.getAddresses().size(); // Force load addresses
        }
        model.addAttribute("customer", customer);
        return "customer-detail";
    }

    @GetMapping("/{id}/edit")
    public String editCustomer(@PathVariable Long id, Model model) {
        addUserNameToModel(model);
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer != null) {
            customer.getAddresses().size(); // Force load addresses
        }
        model.addAttribute("customer", customer);
        return "customer-edit";
    }

    @PostMapping("/{id}")
    public String updateCustomer(@PathVariable Long id, @ModelAttribute Customer customer, 
                               RedirectAttributes redirectAttributes) {
        customer.setId(id);
        customerRepository.save(customer);
        redirectAttributes.addFlashAttribute("message", "Customer updated successfully");
        return "redirect:/customers/" + id;
    }

    @PostMapping("/{id}/addresses")
    public String addAddress(@PathVariable Long id, @ModelAttribute Address address,
                           RedirectAttributes redirectAttributes) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer != null) {
            address.setCustomer(customer);
            addressRepository.save(address);
            redirectAttributes.addFlashAttribute("message", "Address added successfully");
        }
        return "redirect:/customers/" + id + "/edit";
    }

    @PostMapping("/{customerId}/addresses/{addressId}")
    public String updateAddress(@PathVariable Long customerId, @PathVariable Long addressId,
                              @ModelAttribute Address address, RedirectAttributes redirectAttributes) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer != null) {
            address.setId(addressId);
            address.setCustomer(customer);
            addressRepository.save(address);
            redirectAttributes.addFlashAttribute("message", "Address updated successfully");
        }
        return "redirect:/customers/" + customerId + "/edit";
    }

    @PostMapping("/{customerId}/addresses/{addressId}/delete")
    public String deleteAddress(@PathVariable Long customerId, @PathVariable Long addressId,
                              RedirectAttributes redirectAttributes) {
        addressRepository.deleteById(addressId);
        redirectAttributes.addFlashAttribute("message", "Address deleted successfully");
        return "redirect:/customers/" + customerId + "/edit";
    }
}