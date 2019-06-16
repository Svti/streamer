package com.streamer.web.crontroller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.streamer.service.context.AppService;
import com.streamer.service.core.StreamerConstant;
import com.streamer.web.rpc.RpcService;

@Controller
public class WebController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private AppService appService;

	@Resource
	private RpcService rpcService;

	@RequestMapping(value = "/web/index")
	public String index(HttpServletRequest request) {
		request.setAttribute("index", appService.findTotal(StreamerConstant.TIMEOUT));
		return "web/index";
	}

	@RequestMapping(value = "/web/login")
	public String login(HttpServletRequest request) {

		HttpSession session = request.getSession();

		if (session.getAttribute("user") != null) {
			return index(request);
		}

		String username = request.getParameter("username");
		String password = request.getParameter("password");

		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
			return "web/login";
		}

		if (appService.login(username, password)) {
			session.setAttribute("user", username);
			return "redirect:index";
		} else {
			return "web/login";
		}
	}

	@RequestMapping(value = "/web/logout")
	public String logout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.invalidate();
		return "web/login";
	}

	@RequestMapping("/node/index")
	public String node_index(HttpServletRequest request) {
		request.setAttribute("nodes", appService.findAlivableNode(StreamerConstant.TIMEOUT));
		return "node/index";
	}

	@RequestMapping("/node/log/{node:.+}")
	public String node_index(@PathVariable String node, HttpServletRequest request) {

		request.setAttribute("node", node);

		// 第N行
		long line = StreamerConstant.LOG_LINE;
		if (StringUtils.isNotEmpty(request.getParameter("line"))) {
			line = Long.parseLong(request.getParameter("line"));
			if (line < StreamerConstant.LOG_LINE) {
				line = StreamerConstant.LOG_LINE;
			}
		}
		request.setAttribute("line", line);

		try {
			String log = rpcService.log(node, StreamerConstant.NULL_JOB, line);
			request.setAttribute("log", log);
		} catch (Exception e) {
			request.setAttribute("log", "");
			logger.info(e.getMessage(), e);
			request.setAttribute(StreamerConstant.STATUS, StreamerConstant.ERROR);
			request.setAttribute(StreamerConstant.MESSAGE, ExceptionUtils.getFullStackTrace(e));
		}

		return "node/log";
	}

}
