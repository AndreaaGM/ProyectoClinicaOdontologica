package BackEndC2.ClinicaOdontologica.controller;

import BackEndC2.ClinicaOdontologica.entity.Odontologo;
import BackEndC2.ClinicaOdontologica.service.OdontologoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class OdontologoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OdontologoService odontologoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void listarTodosLosOdontologos() throws Exception {
        odontologoService.guardarOdontologo(new Odontologo("NS1", "David", "Rios"));
        odontologoService.guardarOdontologo(new Odontologo("NS2", "Ulises", "Guglielmi"));
        odontologoService.guardarOdontologo(new Odontologo("NS3", "Carlos", "Ramirez"));

        MvcResult respuesta = mockMvc.perform(MockMvcRequestBuilders.get("/odontologos")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        assertFalse(respuesta.getResponse().getContentAsString().isEmpty());
    }


    @Test
    public void guardarOdontologo() throws Exception {
        Odontologo odontologo = new Odontologo("NS14", "Laura", "Gomez");

        MvcResult respuesta = mockMvc.perform(MockMvcRequestBuilders.post("/odontologos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(odontologo)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        assertFalse(respuesta.getResponse().getContentAsString().isEmpty());
    }


    @Test
    public void registrarOdontologoConConflicto() throws Exception {
        // Crear un odontólogo con una matrícula que ya existe
        Odontologo odontologoExistente = new Odontologo("NS3", "Juan", "Lopez");
        odontologoService.guardarOdontologo(odontologoExistente);

        // Realizar la petición POST y verificar que se lanza la excepción
        MvcResult respuesta = mockMvc.perform(MockMvcRequestBuilders.post("/odontologos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(odontologoExistente)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isConflict())
                .andReturn();

        // Verificar que el contenido de la respuesta contiene el mensaje de la excepción
        String responseContent = respuesta.getResponse().getContentAsString();
        assertTrue(responseContent.contains("Ya existe un odontologo con matricula MP06"));
    }


    @Test
    public void actualizarOdontologo() throws Exception {

        Odontologo odontologoExistente = new Odontologo("NS2", "Carlos", "Lopez");

        Odontologo odontologoGuardado = odontologoService.guardarOdontologo(odontologoExistente);

        Odontologo odontologoActualizado = new Odontologo(odontologoGuardado.getId(),"MP40", "Carlos", "Lopez Actualizado");


        MvcResult respuesta = mockMvc.perform(MockMvcRequestBuilders.put("/odontologos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(odontologoActualizado)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
        assertTrue(respuesta.getResponse().getContentAsString().contains("Odontologo actualizado"));

        Odontologo odontologoVerificado = odontologoService.buscarPorID(odontologoGuardado.getId()).orElse(null);
        assertTrue(odontologoVerificado != null && "Lopez Actualizado".equals(odontologoVerificado.getApellido()));
    }


    @Test
    public void buscarOdontologoPorId() throws Exception {
        Odontologo odontologo = new Odontologo("NS20", "Laura", "Martinez");

        Odontologo odontologoGuardado = odontologoService.guardarOdontologo(odontologo);

        MvcResult respuesta = mockMvc.perform(MockMvcRequestBuilders.get("/odontologos/{id}", odontologoGuardado.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        Odontologo odontologoEncontrado = objectMapper.readValue(respuesta.getResponse().getContentAsString(), Odontologo.class);

        assertTrue(odontologoEncontrado != null);
        assertTrue(odontologoEncontrado.getId().equals(odontologoGuardado.getId()));
        assertTrue(odontologoEncontrado.getNombre().equals(odontologoGuardado.getNombre()));
        assertTrue(odontologoEncontrado.getApellido().equals(odontologoGuardado.getApellido()));
        assertTrue(odontologoEncontrado.getNumeroMatricula().equals(odontologoGuardado.getNumeroMatricula()));
    }


    @Test
    public void eliminarOdontologo() throws Exception {
        Odontologo odontologo = new Odontologo("NS15", "Pedro", "Gonzalez");

        Odontologo odontologoGuardado = odontologoService.guardarOdontologo(odontologo);

        MvcResult respuesta = mockMvc.perform(MockMvcRequestBuilders.delete("/odontologos/eliminar/{id}", odontologoGuardado.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        assertTrue(respuesta.getResponse().getContentAsString().contains("odontologo eliminado con exito"));

        assertFalse(odontologoService.buscarPorID(odontologoGuardado.getId()).isPresent());
    }
}

