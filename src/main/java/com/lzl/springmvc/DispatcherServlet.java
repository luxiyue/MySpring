package com.lzl.springmvc;

import com.lzl.spring.LzlApplicationContext;
import com.lzl.userapp.config.AppConfig;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author： Luzelong
 * @Created： 2023/10/26 20:07
 */
public class DispatcherServlet extends HttpServlet {

    private static List<HandlerMapping> handlerMappings = new ArrayList<>();
    private static List<HandlerAdapter> handlerAdapters = new ArrayList<>();
    private String configClass;



    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatch(req, resp);
    }




    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) {
        try {
            HandlerExecutionChain mappedHandler = getHandler(req);
            HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());
            Object mv = ha.handle(req, resp, mappedHandler.getHandler());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        if (handlerMappings != null) {
            for (HandlerMapping mapping : handlerMappings) {
                //请求传过去确定,判断能不得到一个handler
                HandlerExecutionChain handler = mapping.getHandler(request);
                if (handler != null) {
                    return handler;
                }
            }
        }
        return null;
    }





    protected HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
        if (handlerAdapters != null) {
            for (HandlerAdapter adapter : handlerAdapters) {
                if (adapter.supports(handler)) {
                    return adapter;
                }
            }
        }
        throw new ServletException("No adapter for handler [" + handler + "]: The DispatcherServlet configuration needs to include a HandlerAdapter that supports this handler");
    }



    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            LzlApplicationContext context = new LzlApplicationContext(AppConfig.class);
            Map<String, HandlerMapping> handlerMappingMap = context.getBeansOfType(HandlerMapping.class);
            handlerMappings.addAll(handlerMappingMap.values());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
