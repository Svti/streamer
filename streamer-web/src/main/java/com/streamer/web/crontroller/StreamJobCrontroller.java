package com.streamer.web.crontroller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.streamer.core.parser.SqlParser;
import com.streamer.service.context.AppService;
import com.streamer.service.view.PaginationView;
import com.streamer.web.constant.WebConstant;
import com.streamer.web.rpc.RpcService;

@Controller
@RequestMapping("/job")
@ConditionalOnProperty(name = "node.master", havingValue = "true")
public class StreamJobCrontroller {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private AppService appService;

	@Resource
	private RpcService rpcService;

	@RequestMapping("/index")
	public String index(HttpServletRequest request) {
		// 分页
		int currentPage = 1;
		if (StringUtils.isNotEmpty(request.getParameter("pageNo"))) {
			currentPage = Integer.parseInt(request.getParameter("pageNo"));
		}
		String name = request.getParameter("name");
		if (StringUtils.isEmpty(name)) {
			name = null;
		} else {
			request.setAttribute("name", name);
		}

		String node = request.getParameter("node");
		if (StringUtils.isEmpty(node)) {
			node = null;
		} else {
			request.setAttribute("node", node);
		}

		String sql = request.getParameter("sql");
		if (StringUtils.isEmpty(sql)) {
			sql = null;
		} else {
			request.setAttribute("sql", sql);
		}

		PaginationView<Map<String, Object>> pv = new PaginationView<Map<String, Object>>(WebConstant.PAGE_SIZE,
				currentPage);
		pv.setPaginationResult(appService.findAll(name, node, sql, currentPage, WebConstant.PAGE_SIZE));
		request.setAttribute(WebConstant.PAGEVIEW, pv);
		return "job/index";
	}

	@RequestMapping("/add")
	public String job_add(HttpServletRequest request) {
		request.setAttribute("nodes", appService.findAlivableNode(WebConstant.TIMEOUT));
		return "job/add";
	}

	@RequestMapping("/edit/{name}")
	public String job_edit(HttpServletRequest request, @PathVariable String name) {
		List<Map<String, Object>> list = appService.findJobByName(name);

		// 放入可用节点
		request.setAttribute("nodes", appService.findAlivableNode(WebConstant.TIMEOUT));

		if (list.isEmpty()) {
			return "job/add";
		} else {
			request.setAttribute("job", list.get(0));
			return "job/add";
		}
	}

	@RequestMapping("/view/{name}")
	public String job_view(HttpServletRequest request, @PathVariable String name) {
		List<Map<String, Object>> list = appService.findJobByName(name);

		// 放入可用节点
		request.setAttribute("nodes", appService.findAlivableNode(WebConstant.TIMEOUT));

		if (list.isEmpty()) {
			return "job/add";
		} else {
			request.setAttribute("job", list.get(0));
			request.setAttribute("readonly", "readonly");
			return "job/add";
		}
	}

	@RequestMapping("/del/{name}")
	public String job_del(HttpServletRequest request, @PathVariable String name) {
		List<Map<String, Object>> list = appService.findJobByName(name);
		if (list.isEmpty()) {
			return "redirect:/job/index";
		} else {

			Map<String, Object> map = list.get(0);
			int status = Integer.valueOf(map.get("status").toString());
			if (status > 0) {
				request.setAttribute(WebConstant.STATUS, WebConstant.ERROR);
				request.setAttribute(WebConstant.MESSAGE, "任务" + name + "正在运行中，不能删除。");

				int currentPage = 1;
				if (StringUtils.isNotEmpty(request.getParameter("pageNo"))) {
					currentPage = Integer.parseInt(request.getParameter("pageNo"));
				}
				PaginationView<Map<String, Object>> pv = new PaginationView<Map<String, Object>>(WebConstant.PAGE_SIZE,
						currentPage);
				pv.setPaginationResult(appService.findAll(null, null, null, currentPage, WebConstant.PAGE_SIZE));
				request.setAttribute(WebConstant.PAGEVIEW, pv);

				return "job/index";
			} else {
				appService.del(name);
				return "redirect:/job/index";
			}
		}
	}

	@RequestMapping("/save")
	public String job_save(HttpServletRequest request) {

		String queryName = request.getParameter("name");
		String sql = request.getParameter("sql");
		String node = request.getParameter("node");

		if (com.mysql.jdbc.StringUtils.isEmptyOrWhitespaceOnly(queryName)) {
			request.setAttribute(WebConstant.STATUS, WebConstant.ERROR);
			request.setAttribute(WebConstant.MESSAGE, "任务名不能为空");
			return "job/add";
		}

		if (com.mysql.jdbc.StringUtils.isEmptyOrWhitespaceOnly(node)) {
			request.setAttribute(WebConstant.STATUS, WebConstant.ERROR);
			request.setAttribute(WebConstant.MESSAGE, "请选择执行节点");
			job_edit(request, queryName);
			return "job/add";
		}

		Matcher matcher = Pattern.compile("^\\w+$").matcher(queryName);

		if (!matcher.matches()) {
			request.setAttribute(WebConstant.STATUS, WebConstant.ERROR);
			request.setAttribute(WebConstant.MESSAGE, "任务名只能为数字和字母");
			job_edit(request, queryName);
			return "job/add";
		}

		if (StringUtils.length(queryName) < 3) {
			request.setAttribute(WebConstant.STATUS, WebConstant.ERROR);
			request.setAttribute(WebConstant.MESSAGE, "任务名必须大于3个字符");
			job_edit(request, queryName);
			return "job/add";
		}

		if (com.mysql.jdbc.StringUtils.isEmptyOrWhitespaceOnly(sql)) {
			request.setAttribute(WebConstant.STATUS, WebConstant.ERROR);
			request.setAttribute(WebConstant.MESSAGE, "配置SQL不能为空");
			job_edit(request, queryName);
			return "job/add";
		}

		boolean flag = appService.exsit(queryName);

		if (flag) {

			try {
				SqlParser.parseSql(queryName, sql);
				appService.update(node, queryName, sql);
				request.setAttribute(WebConstant.STATUS, WebConstant.SUCCESS);
				request.setAttribute(WebConstant.MESSAGE, "任务" + queryName + "修改成功");

				// 分页
				int currentPage = 1;
				if (StringUtils.isNotEmpty(request.getParameter("pageNo"))) {
					currentPage = Integer.parseInt(request.getParameter("pageNo"));
				}
				PaginationView<Map<String, Object>> pv = new PaginationView<Map<String, Object>>(WebConstant.PAGE_SIZE,
						currentPage);
				pv.setPaginationResult(appService.findAll(null, null, null, currentPage, WebConstant.PAGE_SIZE));
				request.setAttribute(WebConstant.PAGEVIEW, pv);

				return "job/index";

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				request.setAttribute(WebConstant.STATUS, WebConstant.ERROR);
				request.setAttribute(WebConstant.MESSAGE, ExceptionUtils.getFullStackTrace(e));
				job_edit(request, queryName);
				return "job/add";
			}

		} else {
			try {
				SqlParser.parseSql(queryName, sql);
				appService.save(node, queryName, sql, DateTime.now().toDate());
				request.setAttribute(WebConstant.STATUS, WebConstant.SUCCESS);
				request.setAttribute(WebConstant.MESSAGE, "任务" + queryName + "保存成功");

				// 分页
				int currentPage = 1;
				if (StringUtils.isNotEmpty(request.getParameter("pageNo"))) {
					currentPage = Integer.parseInt(request.getParameter("pageNo"));
				}
				PaginationView<Map<String, Object>> pv = new PaginationView<Map<String, Object>>(WebConstant.PAGE_SIZE,
						currentPage);
				pv.setPaginationResult(appService.findAll(null, null, null, currentPage, WebConstant.PAGE_SIZE));
				request.setAttribute(WebConstant.PAGEVIEW, pv);
				return "job/index";

			} catch (Exception e) {
				request.setAttribute(WebConstant.STATUS, WebConstant.ERROR);
				request.setAttribute(WebConstant.MESSAGE, ExceptionUtils.getFullStackTrace(e));
				logger.error(e.getMessage(), e);
				job_edit(request, queryName);
				return "job/add";
			}
		}
	}

	@RequestMapping("/run/{node}/{name}")
	public String job_run(HttpServletRequest request, @PathVariable String node, @PathVariable String name) {

		List<Map<String, Object>> list = appService.findJobByName(name);

		if (list.isEmpty()) {
			request.setAttribute(WebConstant.STATUS, WebConstant.ERROR);
			request.setAttribute(WebConstant.MESSAGE, "任务" + name + "不存在");
		} else {

			Map<String, Object> map = list.get(0);

			int status = Integer.valueOf(map.get("status").toString());

			if (status > 0) {
				request.setAttribute(WebConstant.STATUS, WebConstant.ERROR);
				request.setAttribute(WebConstant.MESSAGE, "任务" + name + "正在运行中");
			} else {
				try {
					String txt = String.valueOf(map.get("sql"));
					// 校验一次
					SqlParser.parseSql(name, txt);

					// 调用执行节点
					boolean flag = rpcService.run(node, name);

					if (flag) {
						request.setAttribute(WebConstant.STATUS, WebConstant.SUCCESS);
						request.setAttribute(WebConstant.MESSAGE, "任务" + name + "启动成功");
					} else {
						request.setAttribute(WebConstant.STATUS, WebConstant.ERROR);
						request.setAttribute(WebConstant.MESSAGE, "任务" + name + "启动失败");
					}

				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					request.setAttribute(WebConstant.STATUS, WebConstant.ERROR);
					request.setAttribute(WebConstant.MESSAGE, ExceptionUtils.getFullStackTrace(e));
				}
			}
		}

		// 分页
		int currentPage = 1;
		if (StringUtils.isNotEmpty(request.getParameter("pageNo"))) {
			currentPage = Integer.parseInt(request.getParameter("pageNo"));
		}
		PaginationView<Map<String, Object>> pv = new PaginationView<Map<String, Object>>(WebConstant.PAGE_SIZE,
				currentPage);
		pv.setPaginationResult(appService.findAll(null, null, null, currentPage, WebConstant.PAGE_SIZE));
		request.setAttribute(WebConstant.PAGEVIEW, pv);

		return "job/index";
	}

	@RequestMapping("/stop/{node}/{name}")
	public String stop_job(HttpServletRequest request, @PathVariable String node, @PathVariable String name) {

		List<Map<String, Object>> list = appService.findJobByName(name);

		if (list.isEmpty()) {

			request.setAttribute(WebConstant.STATUS, WebConstant.ERROR);
			request.setAttribute(WebConstant.MESSAGE, "任务" + name + "不存在");

		} else {

			Map<String, Object> map = list.get(0);
			int status = Integer.valueOf(map.get("status").toString());

			if (status > 0) {

				try {
					// 调用执行节点
					boolean flag = rpcService.stop(node, name);

					if (flag) {
						request.setAttribute(WebConstant.STATUS, WebConstant.SUCCESS);
						request.setAttribute(WebConstant.MESSAGE, "任务" + name + "停止成功");
					} else {
						request.setAttribute(WebConstant.STATUS, WebConstant.ERROR);
						request.setAttribute(WebConstant.MESSAGE, "任务" + name + "停止失败");
					}

				} catch (Exception e) {
					logger.info(e.getMessage(), e);
					request.setAttribute(WebConstant.STATUS, WebConstant.ERROR);
					request.setAttribute(WebConstant.MESSAGE, ExceptionUtils.getFullStackTrace(e));
				}
			} else {
				request.setAttribute(WebConstant.STATUS, WebConstant.ERROR);
				request.setAttribute(WebConstant.MESSAGE, "任务" + name + "已经是停止状态");
			}

		}

		// 分页
		int currentPage = 1;
		if (StringUtils.isNotEmpty(request.getParameter("pageNo"))) {
			currentPage = Integer.parseInt(request.getParameter("pageNo"));
		}
		PaginationView<Map<String, Object>> pv = new PaginationView<Map<String, Object>>(WebConstant.PAGE_SIZE,
				currentPage);
		pv.setPaginationResult(appService.findAll(null, null, null, currentPage, WebConstant.PAGE_SIZE));
		request.setAttribute(WebConstant.PAGEVIEW, pv);

		return "job/index";
	}

	@RequestMapping("/log/{node}/{name}")
	public String job_log(HttpServletRequest request, @PathVariable String node, @PathVariable String name) {

		request.setAttribute("node", node);
		request.setAttribute("job", name);

		// 第N行
		long line = 50;
		if (StringUtils.isNotEmpty(request.getParameter("line"))) {
			line = Long.parseLong(request.getParameter("line"));
			if (line < 50) {
				line = 50;
			}
		}
		request.setAttribute("line", line);

		try {
			if (name.equals("_")) {
				String log = rpcService.log(node, "_", line);
				request.setAttribute("log", log);
			} else {
				List<Map<String, Object>> list = appService.findJobByName(name);
				if (list.isEmpty()) {
					request.setAttribute(WebConstant.STATUS, WebConstant.ERROR);
					request.setAttribute(WebConstant.MESSAGE, "任务" + name + "不存在");
					request.setAttribute("log", "");
				} else {
					String log = rpcService.log(node, name, line);
					request.setAttribute("log", log);
				}
			}
		} catch (IOException e) {
			request.setAttribute("log", "");
			logger.info(e.getMessage(), e);
			request.setAttribute(WebConstant.STATUS, WebConstant.ERROR);
			request.setAttribute(WebConstant.MESSAGE, ExceptionUtils.getFullStackTrace(e));
		}
		return "job/log";
	}
}
