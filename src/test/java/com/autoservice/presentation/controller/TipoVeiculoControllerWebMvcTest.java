package com.autoservice.presentation.controller;

import com.autoservice.application.tipoveiculo.create.CadastrarTipoVeiculoCommand;
import com.autoservice.application.tipoveiculo.create.CadastrarTipoVeiculoOutput;
import com.autoservice.application.tipoveiculo.create.CadastrarTipoVeiculoUseCase;
import com.autoservice.application.tipoveiculo.delete.RemoverTipoVeiculoUseCase;
import com.autoservice.application.tipoveiculo.query.GetTipoVeiculoByIdQuery;
import com.autoservice.application.tipoveiculo.query.ListTipoVeiculoQuery;
import com.autoservice.application.tipoveiculo.update.AtualizarTipoVeiculoUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TipoVeiculoController.class)
class TipoVeiculoControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CadastrarTipoVeiculoUseCase cadastrarTipoVeiculoUseCase;

    @MockitoBean
    private ListTipoVeiculoQuery listTipoVeiculoQuery;

    @MockitoBean
    private GetTipoVeiculoByIdQuery getTipoVeiculoByIdQuery;

    @MockitoBean
    private AtualizarTipoVeiculoUseCase atualizarTipoVeiculoUseCase;

    @MockitoBean
    private RemoverTipoVeiculoUseCase removerTipoVeiculoUseCase;

    @Test
    @DisplayName("POST delega ao caso de uso e serializa resposta")
    void postDelegaAoCasoDeUso() throws Exception {
        when(cadastrarTipoVeiculoUseCase.execute(any(CadastrarTipoVeiculoCommand.class)))
                .thenReturn(new CadastrarTipoVeiculoOutput("id-1", "Fiat", "Uno", 2015));

        mockMvc.perform(post("/tipos-veiculo")
                        .with(user("admin@autoservice.local").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"marca\":\"Fiat\",\"modelo\":\"Uno\",\"ano\":2015}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("id-1"))
                .andExpect(jsonPath("$.marca").value("Fiat"))
                .andExpect(jsonPath("$.modelo").value("Uno"))
                .andExpect(jsonPath("$.ano").value(2015));

        verify(cadastrarTipoVeiculoUseCase).execute(any(CadastrarTipoVeiculoCommand.class));
    }
}
