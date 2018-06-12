/*
 * Copyright 2014-2015 the original author or authors.
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
package kickstart.controller;

import kickstart.model.Menu.Menu;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.ModelMap;

@Controller
public class WelcomeController {



	@RequestMapping("/")
	public String index(ModelMap modelMap) {
		Menu menu = new Menu();
		modelMap.addAttribute("menu", menu.getmenu());
		return "index";
	}

	@RequestMapping("/index.html")
	public String index2(ModelMap modelMap) {
		Menu menu = new Menu();
		modelMap.addAttribute("menu", menu.getmenu());

		return "index";
	}
}
