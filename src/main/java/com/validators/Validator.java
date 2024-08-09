package com.validators;

import com.models.UserDetails;

public class Validator {
	
	private String error;
	
	public boolean isValidUser(UserDetails user) {
		if(!hasText(user.getEmail())) {
			error = "Invalid Email";
		}
		else if(!hasText(user.getUsername())){
			error = "Invalid Username";
		}
		else if(!hasText(user.getPassword())) {
			error = "Invalid Password";
		}
		else {
			return true;
		}
		return false;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
	
	public static boolean hasText(String text) {
		return hasText(text, 0);
	}
	
	public static boolean hasText(String text, int length) {
		return text != null && text.length() >= length;
	}
	
}
