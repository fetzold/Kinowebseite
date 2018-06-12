/*
 * Copyright 2013-2014 the original author or authors.
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
package kickstart.validation;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Validates all the input during a registration
 *
 */
public class RegistrationForm {

	@NotBlank(message = "{Form.NotEmpty}")
	private String forename;

	@NotBlank(message = "{Form.NotEmpty}")
	private String name;

	@NotBlank(message = "{Form.NotEmpty}")
	private String password;

	@NotBlank(message = "{Form.NotEmpty}")
	@Pattern(regexp = "[A-Za-z_0-9.!#$%&'*+/=?^_`{|}~-]+\\@[A-Za-z_0-9-]+(\\.[a-zA-Z0-9-]+)+", message = "{Form.NotEmail}")
	private String email;
	
	@NotBlank(message = "{Form.NotEmpty}")
	@Pattern(regexp = "\\d+", message = "{Form.NotNumber}")
	private String phone;

	private Boolean delete;

	public Boolean getDelete() {
		return delete;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}



	
	//shit by Marco


	public void setDelete(Boolean delete) {
		this.delete = delete;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}


	public String getForename() {
		return forename;
	}

	public void setForename(String forename) {
		this.forename = forename;
	}
}
