package mocean.logs.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import mocean.logs.common.Common;
import mocean.logs.domain.UserBean;

@Component
public class LoginInterceptor extends HandlerInterceptorAdapter{

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)throws Exception {
//        HttpSession session = request.getSession();
//        UserBean bean = (UserBean) session.getAttribute(Common.USER);
//        if("/login".equals(request.getServletPath())) {
        	return true;
//        }
//        if(bean == null) {
//        	response.sendRedirect("/login");
//        	return false;
//        }else {
//        	return true;
//        }
	}
}
