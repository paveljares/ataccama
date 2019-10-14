package cz.jares.pavel.web.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @author jaresp
 *
 */
@Controller
@RequestMapping("/api")
public class ApiController {

	@Value("${app.version}")
	private String version;
	
	/**
	 * Get version of application (see build)
	 * 
	 * @return version of application
	 */
	@RequestMapping(value="/version", method=RequestMethod.GET)
	@ResponseBody
	public String getVersion() {
		return version;
	}
	
}
