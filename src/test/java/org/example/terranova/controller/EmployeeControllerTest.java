package org.example.terranova.controller;

import org.example.terranova.model.Employee;
import org.example.terranova.service.EmployeeService;
import org.example.terranova.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EmployeeControllerTest {

    private EmployeeService employeeService;
    private UserService userService;
    private EmployeeController employeeController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        employeeService = mock(EmployeeService.class);
        userService = mock(UserService.class);
        employeeController = new EmployeeController(employeeService, userService);
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
    }

    @Test
    void testListEmployees_withoutSort() throws Exception {
        when(employeeService.getAllEmployeesWithUsers()).thenReturn(List.of(new Employee()));

        mockMvc.perform(get("/employee"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee/employee_list"))
                .andExpect(model().attributeExists("employees"))
                .andExpect(model().attribute("sortBy", (String) null))
                .andExpect(model().attribute("sortDir", (String) null));

        verify(employeeService, times(1)).getAllEmployeesWithUsers();
    }

    @Test
    void testListEmployees_withSort() throws Exception {
        when(employeeService.getAllEmployeesSorted(any(Sort.class))).thenReturn(List.of(new Employee()));

        mockMvc.perform(get("/employee")
                        .param("sortBy", "surname")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee/employee_list"))
                .andExpect(model().attributeExists("employees"))
                .andExpect(model().attribute("sortBy", "surname"))
                .andExpect(model().attribute("sortDir", "asc"));

        verify(employeeService, times(1)).getAllEmployeesSorted(any(Sort.class));
    }

    @Test
    void testShowAddForm() throws Exception {
        when(userService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/employee/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee/employee_form"))
                .andExpect(model().attributeExists("employee"))
                .andExpect(model().attributeExists("users"));

        verify(userService, times(1)).findAll();
    }

    @Test
    void testSaveEmployee_valid() throws Exception {
        Employee employee = new Employee();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = employeeController.saveEmployee(employee, bindingResult, mock(Model.class));
        assertThat(view).isEqualTo("redirect:/employee");
        verify(employeeService, times(1)).saveEmployee(employee);
    }

    @Test
    void testSaveEmployee_withErrors() throws Exception {
        Employee employee = new Employee();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        Model model = mock(Model.class);
        when(userService.findAll()).thenReturn(List.of());

        String view = employeeController.saveEmployee(employee, bindingResult, model);
        assertThat(view).isEqualTo("employee/employee_form");
        verify(model).addAttribute(eq("users"), any());
        verify(employeeService, never()).saveEmployee(any());
    }

    @Test
    void testSaveEmployee_saveThrowsException() throws Exception {
        Employee employee = new Employee();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        doThrow(new RuntimeException("DB error")).when(employeeService).saveEmployee(employee);

        Model model = mock(Model.class);
        when(userService.findAll()).thenReturn(List.of());

        String view = employeeController.saveEmployee(employee, bindingResult, model);
        assertThat(view).isEqualTo("employee/employee_form");
        verify(model).addAttribute(eq("validationErrors"), any());
        verify(model).addAttribute(eq("users"), any());
    }

    @Test
    void testShowEditForm_existingEmployee() throws Exception {
        Employee employee = new Employee();
        when(employeeService.getEmployeeById(1L)).thenReturn(employee);
        when(userService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/employee/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee/employee_form"))
                .andExpect(model().attribute("employee", employee))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    void testShowEditForm_notFound() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(null);

        mockMvc.perform(get("/employee/edit/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employee"));
    }

    @Test
    void testDeleteEmployee_success() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(get("/employee/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employee"));

        verify(employeeService).deleteEmployee(1L);
    }

    @Test
    void testDeleteEmployee_withError() throws Exception {
        doThrow(new IllegalStateException("Employee is involved in a deal.")).when(employeeService).deleteEmployee(1L);

        mockMvc.perform(get("/employee/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employee?error=true"));
    }

    @Test
    void testDeleteAllEmployees_someNotDeleted() throws Exception {
        Employee e1 = new Employee();
        Employee e2 = new Employee();
        when(employeeService.deleteAllExceptWithDeals()).thenReturn(List.of(e1, e2));

        mockMvc.perform(get("/employee/deleteAll"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/employee?partialError=2"));
    }

    @Test
    void testDeleteAllEmployees_noneNotDeleted() throws Exception {
        when(employeeService.deleteAllExceptWithDeals()).thenReturn(List.of());

        mockMvc.perform(get("/employee/deleteAll"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employee"));
    }

    @Test
    void testGetPatronymicByUserId_found() {
        Employee employee = new Employee();
        employee.setPatronymic("Ivanovich");
        when(employeeService.getEmployeeByUserId(10L)).thenReturn(employee);

        ResponseEntity<Map<String, String>> response = employeeController.getPatronymicByUserId(10L);
        assertThat(response.getBody()).containsEntry("patronymic", "Ivanovich");
    }

    @Test
    void testGetPatronymicByUserId_notFound() {
        when(employeeService.getEmployeeByUserId(10L)).thenReturn(null);

        ResponseEntity<Map<String, String>> response = employeeController.getPatronymicByUserId(10L);
        assertThat(response.getBody()).containsEntry("patronymic", "");
    }
}
