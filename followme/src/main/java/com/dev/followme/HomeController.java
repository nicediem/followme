package com.dev.followme;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	static ApplicationContext app;
	static EntityManagerFactory factory;
	static EntityManager manager;
	static EntityTransaction transaction;
	
	// 최초 진입
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		// 최초 1회 연결
		app = new ClassPathXmlApplicationContext("dbbean.xml");
		factory = app.getBean(EntityManagerFactory.class);
		manager = factory.createEntityManager();
		transaction = manager.getTransaction();
		
		return "home";
	}
	
	// 로그인 체크
	@RequestMapping(value = "/login.do", method = RequestMethod.POST)
	public ModelAndView main(Locale locale, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception{;
		String id   = request.getParameter("id") !=""? request.getParameter("id"):"";
		String pwd  = request.getParameter("pwd")!=""? request.getParameter("pwd"):"";
		
		Map<String,Object> modelMap = new HashMap<String,Object>();
	    if(StringUtils.isEmpty(id) || StringUtils.isEmpty(pwd)){
	    	modelMap.put("err","true");
	    	modelMap.put("msg","입력한 회원정보가 일치하지 않습니다.\\n다시 입력해주세요.");
	    	return new ModelAndView(new MappingJackson2JsonView(),modelMap);
	    }
	
        // 사용자 체크		 
		Query query = manager.createQuery("select a from User a where a.id=:id and a.pwd=:pwd");
		
		// pwd 암호화 처리
	    Encoder encoder = Base64.getEncoder();
	    byte[] targetBytes = pwd.getBytes("UTF-8");
	    byte[] encodeBytes = encoder.encode(targetBytes);
		
	    query.setParameter("id", id);
	    query.setParameter("pwd", new String(encodeBytes));
		List<Object[]> list = query.getResultList();
		
		if( list.size() > 0 ) {
			// 사용자 쿠키 생성
			Cookie cookie = new Cookie("userId",id);
			cookie.setPath("/");
			cookie.setMaxAge(60*60*1);
			
			response.addCookie(cookie);
			
			modelMap.put("err","false");
			modelMap.put("msg","로그인 되었습니다.");
		}else {
			modelMap.put("err","true");
			modelMap.put("msg","입력한 회원정보가 일치하지 않습니다.\n다시 입력해주세요.");
		}
		return new ModelAndView(new MappingJackson2JsonView(),modelMap);
	}
	
	// 회원가입페이지
	@RequestMapping(value = "/join", method = RequestMethod.GET)
	public String join(Locale locale, Model model) {
		try {
			if(transaction.isActive()) {}
		}catch(Exception e){
			app = new ClassPathXmlApplicationContext("dbbean.xml");
			factory = app.getBean(EntityManagerFactory.class);
			manager = factory.createEntityManager();
			transaction = manager.getTransaction();
		}
		
		return "join";
	}
		
	// 회원가입
	@RequestMapping(value = "/joinMember.do", method = RequestMethod.POST)
	public ModelAndView joinMember(Locale locale, Model model, HttpServletRequest request) throws UnsupportedEncodingException {
		String id   = request.getParameter("id") !=""?   request.getParameter("id"):"";
		String pwd  = request.getParameter("pwd")!=""?   request.getParameter("pwd"):"";
		String name = request.getParameter("name")!=""?  request.getParameter("name"):"";
		String email= request.getParameter("email")!=""? request.getParameter("email"):"";
		
		Map<String,Object> modelMap = new HashMap<String,Object>();
	    if(StringUtils.isEmpty(id) || StringUtils.isEmpty(pwd) || StringUtils.isEmpty(name) || StringUtils.isEmpty(email)) {
	    	modelMap.put("err","true");
	    	modelMap.put("msg","모든 정보를 입력해주세요.\n(※ 공백불가)");
	    	return new ModelAndView(new MappingJackson2JsonView(),modelMap);
	    }
	    
	    // 중복 회원 가입 체크
		Query query = manager.createQuery("from User where id=:id");
		query.setParameter("id", id);
		List<Object[]> objectList = query.getResultList();
		if( objectList.size() > 0 ) {
			modelMap.put("err","true");
			modelMap.put("msg","이미 가입된 회원입니다.");
			return new ModelAndView(new MappingJackson2JsonView(),modelMap);
		}
		
		// pwd 암호화 처리
	    Encoder encoder = Base64.getEncoder();
	    byte[] targetBytes = pwd.getBytes("UTF-8");
	    byte[] encodeBytes = encoder.encode(targetBytes);
	    
		transaction.begin();
		manager.persist(new User(id, new String(encodeBytes),name,email));
		manager.flush();
		transaction.commit();
		
		modelMap.put("err","false");
		modelMap.put("msg","회원가입을 축하합니다.");
		return new ModelAndView(new MappingJackson2JsonView(),modelMap);
	}
	
	// 메인페이지 이동
	@RequestMapping(value = "/main", method = RequestMethod.GET)
	public String main(Locale locale, Model model, HttpServletRequest request) {
		// 1. 쿠키 값 조회
		Cookie[] cookies = request.getCookies();
		if(cookies != null) {
			for(Cookie c : cookies) {
				if(c.getName().equals("userId")) {
					if(StringUtils.isEmpty(c.getValue())){
						return "home";
					}
				}
			}
		}else {
			return "home";
		}		
		return "main";
	}
	
	// 검색
	@RequestMapping(value = "/search.do", method = RequestMethod.POST)
	public ModelAndView search(Locale locale, Model model, HttpServletRequest request) throws Exception {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		String title = request.getParameter("title") !=""? request.getParameter("title"):"";
		String page = request.getParameter("page") !=""? request.getParameter("page"):"1";
		String pageClick = request.getParameter("pageClick") !=""? request.getParameter("pageClick"):"0";
		String id = "";
		
		if(StringUtils.isEmpty(title)){
			modelMap.put("err","true");
			modelMap.put("msg","검색값을 입력해 주세요.");
	    	return new ModelAndView(new MappingJackson2JsonView(),modelMap);
		}
		
		// 1. 쿠키 값 조회
		Cookie[] cookies = request.getCookies();
		if(cookies != null) {
			for(Cookie c : cookies) {
				if(c.getName().equals("userId")) {
					if(StringUtils.isEmpty(c.getValue())){
						return new ModelAndView("redirect:/followme");
					}else {
						id = c.getValue();
					}
				}
			}
		}else {
			return new ModelAndView("redirect:/followme");
		}
		
		// 1. 내 검색 히스토리 저장, 리턴 메시지 분기 처리 필요		
		if("0".equals(pageClick)){
			transaction.begin();
			MySearchHist mySearchHist = new MySearchHist();
			
			mySearchHist.setId(id);
			mySearchHist.setKeyWord(title);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmdd");
			Calendar c1 = Calendar.getInstance();
			String strToday = sdf.format(c1.getTime());
			mySearchHist.setSearchDt(strToday);
			
			manager.persist(mySearchHist);
			manager.flush();
			transaction.commit();
		}
		// 2. 내 검색 히스토리 조회
		Query query = manager.createQuery("select a.seq, a.id, a.keyWord, a.searchDt from MySearchHist a where a.id=:id order by a.seq desc");
		query.setParameter("id", id);

		List<Object[]> objectList = query.getResultList();
		List<MySearchHistDTO> mySearchHistList= new ArrayList<>();
				
		if( objectList.size() > 0 ) {
			for(Object[] row : objectList) {
				mySearchHistList.add(new MySearchHistDTO((Integer)row[0],(String)row[1],(String)row[2],(String)row[3]));
			}	
			modelMap.put("mySearchHistList",mySearchHistList);
		}

		// 3. 카카오 검색 API 연동
		try {
			String apiURL = "https://dapi.kakao.com/v3/search/book?target=title&page="+page+"&size=10&query="+title;  
			URL url = new URL(apiURL);
			HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Authorization", "KakaoAK 35f06356db32895db079f4ee2c6afb17");
        	con.setUseCaches(false);
	        con.setDoInput(true);
	        con.setDoOutput(true);
        	
	        int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200){ // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8")); 
            }else{  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream(),"UTF-8"));
            }

            String inputLine;
            StringBuffer res = new StringBuffer();
            while ((inputLine = br.readLine()) != null){
            	res.append(inputLine);
            }
            br.close();
            
            // 4. 결과파싱해서 전달하기
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject)jsonParser.parse(res.toString());
            
            String total_count = jsonObject.get("meta").getAsJsonObject().get("total_count").toString();
            String pageable_count = jsonObject.get("meta").getAsJsonObject().get("pageable_count").toString();
            String is_end = jsonObject.get("meta").getAsJsonObject().get("is_end").toString();
            
            modelMap.put("total_count",total_count);
            modelMap.put("pageable_count",pageable_count);
            modelMap.put("is_end",is_end);
            
            JsonArray jsonArray = jsonObject.get("documents").getAsJsonArray();
            List<Documents> dclist = new ArrayList<Documents>();
            
            if( jsonArray.size() > 0 ) {
            	for(JsonElement je : jsonArray) {
            		Documents documents = new Documents();
            		
            		documents.setTitle(je.getAsJsonObject().get("title").toString());
            		documents.setContents(je.getAsJsonObject().get("contents").toString());
            		documents.setUrl(je.getAsJsonObject().get("url").toString());
            		documents.setIsbn(je.getAsJsonObject().get("isbn").toString());
            		documents.setDatetime(je.getAsJsonObject().get("datetime").toString());
            		documents.setPublisher(je.getAsJsonObject().get("publisher").toString());
            		documents.setPrice(Long.parseLong(je.getAsJsonObject().get("price").toString()));
            		documents.setSale_price(Long.parseLong(je.getAsJsonObject().get("sale_price").toString()));
            		documents.setThumbnail(je.getAsJsonObject().get("thumbnail").toString());
            		documents.setStatus(je.getAsJsonObject().get("status").toString());
            		
            		String authors = "";
            		for(JsonElement jr : je.getAsJsonObject().get("authors").getAsJsonArray()) {
            			if(!"[]".equals(jr.getAsString())){
            				authors = authors + jr.getAsString() + " ";
            			}
            		}
            		documents.setAuthors(authors);
            		
            		String translators = "";
            		for(JsonElement jr : je.getAsJsonObject().get("translators").getAsJsonArray()) {
            			if(!"[]".equals(jr.getAsString())){
            				translators = translators + jr.getAsString() + " ";
            			}
            		}
            		documents.setTranslators(translators);
            		
            		dclist.add(documents);
            	}
            	modelMap.put("documentList",dclist);
            }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// 네이버 API 연동 추가
			System.out.println("카카오 API 오류 발생");
            System.out.println(e);
		}
		modelMap.put("err","false");
		modelMap.put("msg","검색되었습니다.");
		return new ModelAndView(new MappingJackson2JsonView(),modelMap);
	}
	
	// 검색
	@RequestMapping(value = "/searchKeyWord.do", method = RequestMethod.POST)
	public ModelAndView searchKeyWord(Locale locale, Model model, HttpServletRequest request) throws Exception {
		try {
			if(transaction.isActive()) {}
		}catch(Exception e){
			app = new ClassPathXmlApplicationContext("dbbean.xml");
			factory = app.getBean(EntityManagerFactory.class);
			manager = factory.createEntityManager();
			transaction = manager.getTransaction();
		}
		
		Map<String,Object> modelMap = new HashMap<String,Object>();
		
		// 1. 내 검색 히스토리 조회
		String id = "";
		Cookie[] cookies = request.getCookies();
		if(cookies != null) {
			for(Cookie c : cookies) {
				if(c.getName().equals("userId")) {
					if(StringUtils.isEmpty(c.getValue())){
						return new ModelAndView("redirect:/followme");
					}else {
						id = c.getValue();
					}
				}
			}
		}else {
			return new ModelAndView("redirect:/followme");
		}
		Query query1 = manager.createQuery("select a.seq, a.id, a.keyWord, a.searchDt from MySearchHist a where a.id=:id order by a.seq desc");
		query1.setParameter("id", id);
		List<Object[]> objectList = query1.getResultList();
		List<MySearchHistDTO> mySearchHistList= new ArrayList<>();
				
		if( objectList.size() > 0 ) {
			for(Object[] row : objectList) {
				mySearchHistList.add(new MySearchHistDTO((Integer)row[0],(String)row[1],(String)row[2],(String)row[3]));
			}	
			modelMap.put("mySearchHistList",mySearchHistList);
		}
		
		// 2. 인기 키워드 목록 조회
		query1 = manager.createQuery("select a.keyWord, count(*) as cnt from MySearchHist a Group by a.keyWord order by count(*) desc");
		query1.setFirstResult(0);
		query1.setMaxResults(10);
		List<Object[]> objectList1 = query1.getResultList();
		List<SearchHistDTO> searchKeyWordList= new ArrayList<>();
		
		if( objectList1.size() > 0 ) {
			for(Object[] row : objectList1) {
				searchKeyWordList.add(new SearchHistDTO((String)row[0],(Long)row[1]));
			}
			modelMap.put("searchKeyWordList",searchKeyWordList);
		}
		
		return new ModelAndView(new MappingJackson2JsonView(),modelMap);
	}
}
