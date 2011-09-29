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
 *  // Assumes static import of: 
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
     * Protected constructor. Not for direct instantiation. 
     * @see org.springframework.test.web.server.setup.MockMvcBuilders
     */
    protected MockMvc(ServletContext servletContext, MvcSetup mvcSetup) {
        this.servletContext = servletContext;
        this.mvcSetup = mvcSetup;
    }

    /**
     * Build a request using the provided {@link RequestBuilder}, execute it,
     * and return a {@link ResultActions} instance that wraps the result.
     * 
	 * @return a ResultActions instance, never {@code null}
	 * @throws Exception if an exception occurs not handled by a HandlerExceptionResolver
	 * 
	 * @see org.springframework.test.web.server.request.MockMvcRequestBuilders
	 * @see org.springframework.test.web.server.result.MockMvcResultActions
     */
    public ResultActions perform(RequestBuilder requestBuilder) throws Exception {
        
    	final MockHttpServletRequest request = requestBuilder.buildRequest(this.servletContext);
        final MockHttpServletResponse response = new MockHttpServletResponse();
        
        MockDispatcher dispatcher = new MockDispatcher(this.mvcSetup);
        dispatcher.execute(request, response);

		final Object handler = dispatcher.getHandler();
		final HandlerInterceptor[] interceptors = dispatcher.getInterceptors();
		final ModelAndView mav = dispatcher.getMav();
		final Exception resolvedException = dispatcher.getResolvedException();

        return new ResultActions() {
        	
			public ResultActions andExpect(ResultMatcher matcher) throws Exception {
				matcher.match(request, response, handler, interceptors, mav, resolvedException);
				return this;
			}
			
			public ResultActions andPrint(ResultPrinter printer) throws Exception {
				printer.print(request, response, handler, interceptors, mav, resolvedException);
				return this;
			}
		};
    }

}
