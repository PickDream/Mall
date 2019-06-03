package com.mmall.common.Interceptor;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.util.JsonUtil;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;


/**
 *
 * @author maoxin
 * */
public class AuthorityInterceptor implements HandlerInterceptor {

    /**
     * 进入Controller之前
     * */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //handler对象包含了要调用方法包括返回值信息等等信息
        HandlerMethod handlerMethod = (HandlerMethod)handler;
        HttpSession session = request.getSession();
        User user = null;
        user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null||(user.getRole().intValue()!=Const.Role.ROLE_ADMIN)){
            response.reset();//这里要进行reset
            //接管了Response
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            if (user==user){
                out.print(JsonUtil.obj2String(ServerResponse.createByError("被拦截器所拦截,用户未登录")));
            }else {
                out.print(JsonUtil.obj2String(ServerResponse.createByError("非管理员")));
            }
            out.flush();
            out.close();//清空
            return false;
        }
        return true;
    }

    /**
     * 进入Controller执行之后
     * */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 整体渲染完成之后才执行
     * */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
