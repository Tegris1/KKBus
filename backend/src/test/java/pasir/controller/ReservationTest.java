package pasir.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Order;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pasir.dtos.ReservationDto;
import pasir.model.TransactionType;
import pasir.services.UserService;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ReservationTest {
    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();
    private UserService userService;

    @Test
    public void testTransaction() throws Exception {
        Map<String,String> loginRequest = new HashMap<String,String>();
        loginRequest.put("email", "test@te.st");
        loginRequest.put("password", "testowe");
        String login1Content = objectMapper.writeValueAsString(loginRequest);

        MvcResult login1 = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(login1Content))
                .andExpect(status().isOk()).andReturn();

        String token1 = JsonPath.read(login1.getResponse().getContentAsString(), "$.token");
        System.out.println("token1: " + token1);

        var transactionDto = new ReservationDto();
        transactionDto.setAmount(100.0);
        transactionDto.setType(TransactionType.INCOME);
        transactionDto.setTags("wyplata");
        transactionDto.setNotes("Pierwsza transakcja");

        String transactionContent = objectMapper.writeValueAsString(transactionDto);
        System.out.println(transactionContent);
        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + token1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionContent))
                .andExpect(status().isOk());


        mockMvc.perform(get("/api/transactions")
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(12.0))
                .andExpect(jsonPath("$[0].type").value("INCOME"));
    }

    @Test
    @Order(1)
    public void noAuthTransaction() throws Exception {
        var transactionDto = new ReservationDto();
        transactionDto.setAmount(500.0);
        transactionDto.setType(TransactionType.INCOME);
        transactionDto.setTags("nieautoryzowana");

        String transactionContent = objectMapper.writeValueAsString(transactionDto);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionContent))
                .andExpect(status().isForbidden());
    }

    @Test
    void noAuthGetTransactions() throws Exception {
        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldForbidUpdatingOtherUsersTransaction() throws Exception {
        Map<String,String> loginRequest = new HashMap<>();
        loginRequest.put("email", "test2@te.st");
        loginRequest.put("password", "testowe");

        String loginContent = objectMapper.writeValueAsString(loginRequest);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginContent))
                .andExpect(status().isOk())
                .andReturn();

        String token2 = JsonPath.read(loginResult.getResponse().getContentAsString(), "$.token");

        var updateDto = new ReservationDto();
        updateDto.setAmount(999.0);
        updateDto.setType(TransactionType.EXPENSE);

        String updateContent = objectMapper.writeValueAsString(updateDto);

        mockMvc.perform(put("/api/transactions/7")
                        .header("Authorization", "Bearer " + token2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateContent))
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldForbidUpdatingTransactionWithoutAuth() throws Exception {
        var updateDto = new ReservationDto();
        updateDto.setAmount(150.0);
        updateDto.setType(TransactionType.EXPENSE);

        String updateContent = objectMapper.writeValueAsString(updateDto);

        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateContent))
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldSuccessfullyDeleteOwnTransaction() throws Exception {
        Map<String,String> loginRequest = new HashMap<>();
        loginRequest.put("email", "test@te.st");
        loginRequest.put("password", "testowe");
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn();
        String token = JsonPath.read(loginResult.getResponse().getContentAsString(), "$.token");

        var newTransaction = new ReservationDto();
        newTransaction.setAmount(300.0);
        newTransaction.setType(TransactionType.EXPENSE);
        newTransaction.setTags("do-usuniecia");

        MvcResult createResult = mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTransaction)))
                .andExpect(status().isOk())
                .andReturn();

        var createdId = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(delete("/api/transactions/" + createdId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldForbidDeletingOtherUsersTransaction() throws Exception {
        Map<String,String> loginUserA = new HashMap<>();
        loginUserA.put("email", "test@te.st");
        loginUserA.put("password", "testowe");
        MvcResult resultUserA = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUserA)))
                .andReturn();
        String tokenA = JsonPath.read(resultUserA.getResponse().getContentAsString(), "$.token");

        Map<String,String> loginUserB = new HashMap<>();
        loginUserB.put("email", "test2@te.st");
        loginUserB.put("password", "testowe");
        MvcResult resultUserB = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUserB)))
                .andReturn();
        String tokenB = JsonPath.read(resultUserB.getResponse().getContentAsString(), "$.token");

        var newTransaction = new ReservationDto();
        newTransaction.setAmount(800.0);
        newTransaction.setType(TransactionType.INCOME);

        MvcResult createResult = mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTransaction)))
                .andExpect(status().isOk())
                .andReturn();

        Integer transactionId = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(delete("/api/transactions/" + transactionId)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isForbidden());
    }
}
