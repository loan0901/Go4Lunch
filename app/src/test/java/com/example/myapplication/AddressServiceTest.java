package com.example.myapplication;

import static org.junit.Assert.assertEquals;

import com.example.myapplication.service.AddressService;

import org.junit.Test;

public class AddressServiceTest {

    @Test
    public void testGetStreetAndNumber_withStreetNumberAndName() {
        String address = "123 Main Street, Springfield, IL";
        String result = AddressService.getStreetAndNumber(address);
        assertEquals("123 Main Street", result);
    }

    @Test
    public void testGetStreetAndNumber_withOnlyStreetName() {
        String address = "Main Street, Springfield, IL";
        String result = AddressService.getStreetAndNumber(address);
        assertEquals("Main Street", result);
    }

    @Test
    public void testGetStreetAndNumber_withEmptyString() {
        String address = "";
        String result = AddressService.getStreetAndNumber(address);
        assertEquals("", result);
    }

    @Test
    public void testGetStreetAndNumber_withWhitespace() {
        String address = "   ";
        String result = AddressService.getStreetAndNumber(address);
        assertEquals("", result.trim());
    }

    @Test
    public void testGetStreetAndNumber_withSpecialCharacters() {
        String address = "123 Main Street @ Springfield, IL";
        String result = AddressService.getStreetAndNumber(address);
        assertEquals("123 Main Street @ Springfield", result);
    }

    @Test
    public void testGetStreetAndNumber_withDifferentFormat() {
        String address = "456 Elm St, Apt 7B, Springfield, IL";
        String result = AddressService.getStreetAndNumber(address);
        assertEquals("456 Elm St", result);
    }

    @Test
    public void testGetStreetAndNumber_withComplexAddress() {
        String address = "789 Maple Ave, Suite 2A, Springfield, IL 62704";
        String result = AddressService.getStreetAndNumber(address);
        assertEquals("789 Maple Ave", result);
    }
}
