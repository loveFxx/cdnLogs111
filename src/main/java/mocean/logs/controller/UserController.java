package mocean.logs.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import mocean.logs.util.Page;
import mocean.logs.util.PageBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import mocean.logs.common.Common;
import mocean.logs.domain.UserBean;

@Controller
public class UserController {

//	@Resource
//	private UserService userService;
	
	@RequestMapping("userLogin")
	public ModelAndView userLogin(String username,String pwd,HttpSession session) {
		ModelAndView mv = new ModelAndView();
//		UserBean user = new UserBean();
//		user.setUsername(username);
//		user.setPassword(DataEncryptionAndDecryptionUtil.getBase64(pwd));
//
//		user = userService.login(user);
		
		session.setMaxInactiveInterval(1800);
//		session.setAttribute(Common.USER, user);
		
//		if(user != null) {
			mv.setViewName("redirect:cdnLogs");
//		}else {
//			mv.addObject("tip", 1);
//			mv.addObject("username", username);
//			mv.addObject("password", pwd);
			mv.setViewName("/login");
//		}
		return mv;
	}

//	@RequestMapping("saveUser")
//	@ResponseBody
//	public int saveUser(String username,String pwd) {
//		UserBean userBean = new UserBean();
//		userBean.setUsername(username);
//		userBean.setPassword(DataEncryptionAndDecryptionUtil.getBase64(pwd));
//		return userService.saveUser(userBean);
//	} s

//	@RequestMapping("updatePwd")
//	@ResponseBody
//	public boolean updatePwd(String username,String pwd) {
//		UserBean userBean = new UserBean();
//		userBean.setUsername(username);
//		userBean.setPassword(DataEncryptionAndDecryptionUtil.getBase64(pwd));
//		return userService.updatePwd(userBean);
//	}
	
	@RequestMapping("getUserlist")
	@ResponseBody
	public JSONObject getUserlist(int rows, int page, PageBean pageBean) {
		pageBean.setStart((page-1)*rows);
		pageBean.setEnd(rows);
		Page<UserBean> usersPage = new Page<>();
//		usersPage.setTotal(userService.getUserCount());
		if(usersPage.getTotal()>0) {
//			usersPage.setRows(userService.getUserList(pageBean));
		}
		return JSON.parseObject(JSON.toJSONString(usersPage));
	}

	@RequestMapping("logout")
	public String logout(HttpSession session) {
		session.removeAttribute(Common.USER);
		return "/login";
	}
}
