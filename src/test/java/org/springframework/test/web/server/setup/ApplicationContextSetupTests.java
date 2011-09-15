package org.springframework.test.web.server.setup;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.MockMvc;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultActions.response;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ApplicationContextSetupTests {

    @Autowired ApplicationContext context;

    @Test
    public void responseBodyHandler(){
        MockMvc mockMvc = MockMvcBuilders.applicationContextMvcSetup(context)
                .configureWarRootDir("src/test/webapp", false).build();

        mockMvc.perform(get("/form"))
            .andExpect(response().status(HttpStatus.OK))
            .andExpect(response().bodyContains("hello"));
    }


}
