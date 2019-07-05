package mocean.logs.controller;

import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import mocean.logs.common.Common;
import mocean.logs.domain.UserBean;

@Controller
public class IndexController {

	@RequestMapping("/cdnLogs")
	public String cdnLogs() {
		return "cdn_logs";
	}

	@RequestMapping("/clientLogs")
	public String clientLogs() {
		return "client_logs";
	}

	@RequestMapping("/cdnRatingtest")
	public String cdnRatingTest() {
		return "cdn_ratingtest";
	}

	@RequestMapping("/cdnRating")
	public String cdnRating() {
		return "cdn_rating";
	}

	@RequestMapping("/cmsLogs")
	public String cmsLogs() {
		return "cms_logs";
	}

	@RequestMapping("/gslbLogs")
	public String gslbLogs() {
		return "gslb_logs";
	}

//
//	@RequestMapping("/cdn_viewers")
//	public String cdn_viewers() {
//		return "cdn_viewers";
//	}
//
//	@RequestMapping("/cdn_viewer_history")
//	public String cdn_viewer_history() {
//		return "cdn_viewers_history";
//	}
//
//	@RequestMapping("/login")
//	public String login() {
//		return "login";
//	}
	
//	@RequestMapping("/admin_cdn_monitor")
//	public ModelAndView admin_cdn_monitor(HttpSession session) {
//		ModelAndView mv = new ModelAndView();
//		UserBean user = (UserBean) session.getAttribute(Common.USER);
//		if(!StringUtils.equals(user.getUsername(), Common.ADMIN)) {
//			mv.addObject("username", user.getUsername());
//			mv.addObject("password", DataEncryptionAndDecryptionUtil.getFromBase64(user.getPassword()));
//			mv.setViewName("updatePwd");
//		}else {
//			mv.setViewName("admin_cdn_monitor");
//		}
//		return mv;
//	}
	
//	@RequestMapping("/cdn_viewers_statistic")
//	public String cdn_viewers_statistic() {
//		return "cdn_viewers_statistic";
//	}

}
