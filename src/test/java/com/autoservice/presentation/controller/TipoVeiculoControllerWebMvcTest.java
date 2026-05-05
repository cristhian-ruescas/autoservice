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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TipoVeiculoController.class)
class TipoVeiculoControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CadastrarTipoVeiculoUseCase cadastrarTipoVeiculoUseCase;

    @MockBean
    private ListTipoVeiculoQuery listTipoVeiculoQuery;

    @MockBean
    private GetTipoVeiculoByIdQuery getTipoVeiculoByIdQuery;

    @MockBean
    private AtualizarTipoVeiculoUseCase atualizarTipoVeiculoUseCase;

    @MockBean
    private RemoverTipoVeiculoUseCase removerTipoVeiculoUseCase;

    @Test
    @DisplayName("POST delega ao caso de uso e serializa resposta")
    void postDelegaAoCasoDeUso() throws Exception {
        when(cadastrarTipoVeiculoUseCase.execute(any(CadastrarTipoVeiculoCommand.class)))
                .thenReturn(new CadastrarTipoVeiculoOutput("id-1", "Fiat", "Uno", 2015));

        mockMvc.perform(post("/tipos-veiculo")
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
