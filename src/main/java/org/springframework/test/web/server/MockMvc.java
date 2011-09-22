/*
 * Copyright 2002-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.test.web.server;

import javax.servlet.ServletContext;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * <strong>Main entry point for server-side Spring MVC test support.</strong> 
 *  
 * <p>Example:
 * <pre>
 *  // Static imports: 
 *  // MockMvcBuilders.*, MockMvcRequestBuilders.*, and MockMvcResultActions.*
 * 
 *  MockMvc mockMvc = 
 *      annotationConfigMvcSetup(TestConfiguration.class)
 *          .configureWarRootDir("src/main/webapp", false).build()
 *  
 *  mockMvc.perform(get("/form"))
 *      .andExpect(response().status().is(HttpStatus.OK))
 *      .andExpect(response().forwardedUrl("/WEB-INF/layouts/main.jsp"));
 *      
 *  mockMvc.perform(post("/form")).andPrintTo(console());
 *      
 * </pre> 
 * 
 *  
 * @author Rossen Stoyanchev
 *  
 * @see org.springframework.test.web.server.setup.MockMvcBuilders
 * @see org.springframework.test.web.server.request.MockMvcRequestBuilders
 * @see org.springframework.test.web.server.result.MockMvcResultActions
 */
public class MockMvc {

    private final ServletContext servletContext;

    private final MvcSetup mvcSetup;

    /** 
     * Protected constructor. See all available {@link MockMvc} builders in:
     * {@code org.springframework.test.web.server.setup.MockMvcBuilders}
     */
    protected MockMvc(ServletContext servletContext, MvcSetup mvcSetup) {
        this.servletContext = servletContext;
        this.mvcSetup = mvcSetup;
    }

    /**
     * Perform a request after building it with the provided {@link RequestBuilder} and 
     * then allow for expectations and other actions to be set up against the results.
     * 
	 * <p>See all available request builders in:
	 * {@code org.springframework.test.web.server.request.MockMvcRequestBuilders}.
	 * 
	 * <p>See all available result actions in:
	 * {@code org.springframework.test.web.server.result.MockMvcResultActions}.
     */
    public ResultActions perform(RequestBuilder requestBuilder) {
        
    	final MockHttpServletRequest request = requestBuilder.buildRequest(this.servletContext);
        final MockHttpServletResponse response = new MockHttpServletResponse();
        
        MockDispatcher dispatcher = new MockDispatcher(this.mvcSetup);
        dispatcher.execute(request, response);

		final Object handler = dispatcher.getHandler();
		final HandlerInterceptor[] interceptors = dispatcher.getInterceptors();
		final ModelAndView mav = dispatcher.getMav();
		final Exception resolvedException = dispatcher.getResolvedException();

        return new ResultActions() {
        	
			public ResultActions andExpect(ResultMatcher matcher) {
				matcher.match(request, response, handler, interceptors, mav, resolvedException);
				return this;
			}
			
			public void andPrintTo(ResultPrinter printer) {
				printer.print(request, response, handler, interceptors, mav, resolvedException);
			}
		};
    }

}
