package com.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.constants.Constants;
import com.models.Link;
import com.models.UserDetails;
import com.repositories.LinkRepository;
import com.repositories.UserRepository;
import com.services.GraphQLService;
import com.validators.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {
	
	@Autowired
	GraphQLService graphQLService;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	LinkRepository linkRepository;
	
	@GetMapping("/leetcode")
	public ResponseEntity<?> leetcode(HttpSession session) throws IOException, URISyntaxException, ParseException {
		Map<String, Object> v = new HashMap<String, Object>();
		Link link = linkRepository.findByUserDetails(new UserDetails((Long) session.getAttribute("userId")));
		JSONObject c = new JSONObject(link.getCredentials());
		v.put("username", c.get("username"));
		String resp = graphQLService.sendGraphQLRequest(Constants.LEETCODE_QUERY, v);
		System.out.println(resp);
		JSONObject matchedUser = new JSONObject(resp).getJSONObject("data").getJSONObject("matchedUser");
		JSONObject subCal = new JSONObject(matchedUser.getString("submissionCalendar"));
		JSONArray acSubNum = matchedUser.getJSONObject("submitStats").getJSONArray("acSubmissionNum");
		long lastSubmit = 0;
		int tdaySubmit = 0, numLastSub = 0;
		int easyCom = 0, hardCom = 0, mediumCom = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Date d = new Date();
		Long cur = (sdf.parse(sdf.format(d)).getTime()/1000) + 19800;
		for(String key: subCal.keySet()) {
			long temp = Long.parseLong(key);
			if(temp > lastSubmit && temp!=cur) {
				numLastSub = subCal.getInt(key);
				lastSubmit = temp;
			}
			if(temp == cur) {
				tdaySubmit = subCal.getInt(cur.toString());
			}
		}
		for(int i=0; i<acSubNum.length(); i++) {
			JSONObject obj = acSubNum.getJSONObject(i);
			if(obj.getString("difficulty").equals("Easy")) {
				easyCom = obj.getInt("count");
			}
			else if(obj.getString("difficulty").equals("Medium")) {
				mediumCom = obj.getInt("count");
			}
			else if(obj.getString("difficulty").equals("Hard")) {
				hardCom = obj.getInt("count");
			}
			
		}
		JSONObject finalResponse = new JSONObject();
		finalResponse.put("lastSubmissionDate", sdf.format(new Date(lastSubmit*1000)));
		finalResponse.put("numOfEasyProbSolved", easyCom);
		finalResponse.put("numOfMediumProbSolved", mediumCom);
		finalResponse.put("numOfHardProbSolved", hardCom);
		finalResponse.put("numOfTotalProbSolved", easyCom+mediumCom+hardCom);
		finalResponse.put("numOfProbSubmittedLast", numLastSub);
		finalResponse.put("numOfProbSumbittedToday", tdaySubmit);
		return ResponseEntity.status(200).body(finalResponse.toString());
	}
	
	
	@PostMapping("/link")
	public ResponseEntity<?> link(@RequestBody Link link, HttpSession session){
		Long userId = (Long) session.getAttribute("userId");
		link.setUser(new UserDetails(userId));
		linkRepository.save(link);
		return ResponseEntity.ok(null);
	}
	
	
	@PostMapping("/signup")
	public ResponseEntity<?> signUp(@RequestBody UserDetails loginReq) {
		Validator v = new Validator();
		Map<String, String> resp = new HashMap<String, String>();
		if(v.isValidUser(loginReq)) {
			userRepository.save(loginReq);
			resp.put("message", "success");
			return ResponseEntity.status(200).body(resp);
		}
		else {
			resp.put("error", v.getError());
			return ResponseEntity.status(500).body(resp);
		}
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody UserDetails loginReq, HttpServletRequest request){
		Map<String, String> resp = new HashMap<String, String>();
		UserDetails user = userRepository.findByEmail(loginReq.getEmail());
		if(user == null) {
			resp.put("error", "Email Doesnt exists");
		}
		else if(!user.getPassword().equals(loginReq.getPassword())) {
			resp.put("error", "Invalid credentials");
		}
		else {
			resp.put("message", "Success");
			HttpSession session = request.getSession();
			session.setMaxInactiveInterval(1200);
			session.setAttribute("userId", user.getId());
			return ResponseEntity.status(200).body(resp);
		}
		return ResponseEntity.status(500).body(resp);
	}
	
}
