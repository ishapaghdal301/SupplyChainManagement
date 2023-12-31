package com.lifecartmain.controller;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.lifecartmain.dao.AdminRepo;
import com.lifecartmain.dao.CartRepo;
import com.lifecartmain.dao.GroceryRepo;
import com.lifecartmain.dao.OrderRepo;
import com.lifecartmain.dao.UserRepo;
import com.lifecartmain.models.Cart;
import com.lifecartmain.models.Grocery;
import com.lifecartmain.models.Orders;
import com.lifecartmain.models.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class LifeCartController {
	@Autowired
	AdminRepo adminRepo;
	@Autowired
	UserRepo userRepo;
	@Autowired
	GroceryRepo gRepo;
	@Autowired
	CartRepo cartRepo;
	@Autowired
	OrderRepo orderRepo;
	@GetMapping("/")
	public ModelAndView home() {
		ModelAndView mv=new ModelAndView();
		mv.setViewName("index");
		return mv;
	}
	
	
//	public String addPage() {
//		return "index";
//	}
	@RequestMapping("/register")
	public String registerGet(@ModelAttribute("User") User user, Model model) {
		model.addAttribute("r_error","");
		return "register";
		
	}
	@RequestMapping("/saveUser")
	public String registerPost(@ModelAttribute("User") User u,Model model){
		try {
		if(u.getPassword()!= null) {
			if(u.getPassword().equals(u.getConfirm_password())) {
//				System.out.println(u.getPassword()+"---------------------------------------------------------------------------------------------------------------------------------");
				
				model.addAttribute("r_error","");
				userRepo.save(u);
				return "redirect:login";
			}
			else {
				model.addAttribute("r_error", "Password does not match with Confirm password!");
				return "register";
			}
		}
		}
		catch(DataIntegrityViolationException e) {
			model.addAttribute("r_error","Please enter unique Email Id");
			return "register";
		}
		return "register";
		
	}
	@RequestMapping("/login")
	public ModelAndView loginPage(Model model,HttpSession session) {
		ModelAndView mv = new ModelAndView();
		session.setAttribute("l_error","no");
		System.out.println("Inside the errorrrr");
//			model.addAttribute("l_error", " elo");
			mv.setViewName("login");
			return mv;
	}
	@RequestMapping("/loginService")
	public String login(String username, String password, HttpSession session,Model model) {
		int exists = userRepo.userExists(username, password);
		System.out.println(exists);
		
		String s="redirect:";
		if(exists == 1) {
//			boolean isAdmin1 = userRepo.isAdmin(username);
			session.setAttribute("username", username);
//			session.setAttribute("isAdmin", isAdmin1);
			s+="/";
			return s;
		}
		else {
			System.out.println("inside else of logservice");
			ModelAndView mv = new ModelAndView();
			session.setAttribute("l_error","Invalid Username or Password");
//			s+="login";
//			return s;
			return "login";
		}
	}
	@RequestMapping("/adminLogin")
	public ModelAndView AdminPage() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("AdminLogin");
		return mv;
	}
	
	@RequestMapping("/adminHome")
	public ModelAndView AdminHome() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("adminHome");
		return mv;
	}
	
	@RequestMapping("/AdminloginService")
	public String Adminlogin(String username, String password, HttpSession session) {
		int exists = adminRepo.AdminExists(username, password);
		System.out.println(exists);
		
		String s="redirect:";
		if(exists == 1) {
//			boolean isAdmin1 = userRepo.isAdmin(username);
			session.setAttribute("username", username);
//			session.setAttribute("isAdmin", isAdmin1);
			return "adminHome";
		}
		else {
			s+="/login";
		}
		return s;
	}
	
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("username");
//		session.removeAttribute("isAdmin");
		session.invalidate();
		return "redirect:/register";
	}
	
	@RequestMapping("/groceries")
	public ModelAndView getGroceries(ModelMap map, @ModelAttribute("Cart") Cart c, HttpSession session) {
		if(session==null) {
			return new ModelAndView("redirect:/login");
		}
		ModelAndView mv= new ModelAndView();
		mv.setViewName("grocery");
		List<Grocery> groceries= gRepo.findAll();
		map.addAttribute("groceries", groceries);
		List<Object[]> cart = cartRepo.findAllByUsername((String)session.getAttribute("username"));
//		System.out.println(cart.get(0)[0]);
		map.addAttribute("cart",cart);
		mv.addObject(map);
		return mv;
	}
	
	@RequestMapping("/addGroceries")
	public ModelAndView groceryAdder(HttpSession session, @ModelAttribute("Grocery") Grocery g, Model model) {
		if(session==null) {
			return new ModelAndView("redirect:/login");
		}
//		System.out.println(session.getAttribute("isAdmin"));
//		if(null != session.getAttribute("isAdmin") && (boolean)session.getAttribute("isAdmin") == false) {
//			return new ModelAndView("redirect:/groceries");
//		}
//		else if(null == session.getAttribute("isAdmin")) {
//			return new ModelAndView("redirect:/login");
//		}
		ModelAndView mv = new ModelAndView();
		mv.setViewName("addGrocery");
		return mv;
	}
	
	@RequestMapping("/setGroceries")
	public ModelAndView setGroceries(HttpSession session, @ModelAttribute("Grocery") Grocery g) {
		if(session==null) {
			return new ModelAndView("redirect:/login");
		}
		session.setAttribute("product", g);
		gRepo.save(g);
		return new ModelAndView("redirect:/adminHome");
	}
	
	@RequestMapping("/addToCart")
	public String addToCart(@ModelAttribute("Cart") Cart c, HttpSession session) {
		if(session==null) {
			return "redirect:/login";
		}
		c.setUsername((String)session.getAttribute("username"));
		cartRepo.save(c);
		return "redirect:/groceries";
	}
	
	@RequestMapping("/removeFromCart")
	public String removeFromCart(@ModelAttribute("Cart") Cart c, HttpSession session) {
		if(session==null) {
			return "redirect:/login";
		}
		cartRepo.deleteItem(c.getId());
		return "redirect:/groceries";
	}
	
	@RequestMapping("/checkout")
	public ModelAndView checkout(ModelMap map, HttpSession session) {
		if(session==null) {
			return new ModelAndView("redirect:/login");
		}
		String username = (String)session.getAttribute("username");
		List<Object[]> cart = cartRepo.findAllByUsername(username);
//		System.out.println(cart);
		for(int i = 0;i<cart.size();i++) {
//			System.out.println(cart.get(i)[1]);
//			System.out.println(cart.get(i)[2]);
			long prodID = (long)(cart.get(i)[1]);
			int quantity = (int)(cart.get(i)[2]);
			int tmp = (int)gRepo.getQuantity(prodID);
			quantity = Math.min(quantity, tmp);
			if(quantity < 0) {
				quantity = 0;
			}
			gRepo.update(tmp-quantity,prodID);
			double price = (Double)gRepo.getSellPrice(prodID);
			Date currDate = new Date();
			Orders order = new Orders(username,prodID,quantity,quantity*price,currDate);
//			order.setPrice(price);
//			order.setProdID(prodID);
//			order.setQuantity(quantity);
//			order.setUsername(username);
			orderRepo.save(order);
			cartRepo.delete(username);
		}
		ModelAndView mv=new ModelAndView();
		mv.setViewName("checkout");
		List<Object[]> orders = orderRepo.findAllByUsername(username);
		map.addAttribute("orders", orders);
		mv.addObject(map);
		return mv;
	}
	@RequestMapping("/viewOrders")
	public ModelAndView viewOrders(ModelMap map, HttpSession session) {
		if(session==null) {
			return new ModelAndView("redirect:/login");
		}
//		if(null != session.getAttribute("isAdmin") && (boolean)session.getAttribute("isAdmin") == false) {
//			return new ModelAndView("redirect:/");
//		}
//		else if(null == session.getAttribute("isAdmin")) {
//			return new ModelAndView("redirect:/login");
//		}
		ModelAndView mv = new ModelAndView();
		mv.setViewName("allOrders");
		List<Orders> orders = orderRepo.findAll();
		map.addAttribute("orders",orders);
		mv.addObject(map);
		return mv;
	}
	@RequestMapping("/viewCustomers")
	public ModelAndView viewCustomers(ModelMap map, HttpSession session) {
		if(session==null) {
			return new ModelAndView("redirect:/login");
		}
		
//		if(null != session.getAttribute("isAdmin") && (boolean)session.getAttribute("isAdmin") == false) {
//			return new ModelAndView("redirect:/");
//		}
//		else if(null == session.getAttribute("isAdmin")) {
//			return new ModelAndView("redirect:/login");
//		}
		ModelAndView mv = new ModelAndView();
		mv.setViewName("allCustomers");
		List<User> users = userRepo.findAll();
		map.addAttribute("users",users);
		mv.addObject(map);
		return mv;
	}
	@RequestMapping("/viewInventory")
	public ModelAndView viewInventory(ModelMap map, HttpSession session,@ModelAttribute("Grocery") Grocery c) {
		if(session==null) {
			return new ModelAndView("redirect:/login");
		}
//		if(null != session.getAttribute("isAdmin") && (boolean)session.getAttribute("isAdmin") == false) {
//			return new ModelAndView("redirect:/");
//		}
//		else if(null == session.getAttribute("isAdmin")) {
//			return new ModelAndView("redirect:/login");
//		}
		ModelAndView mv= new ModelAndView();
		mv.setViewName("inventory");
		List<Grocery> groceries = gRepo.findAll();
		map.addAttribute("groceries", groceries);
		mv.addObject(map);
		return mv;
	}
	
	@RequestMapping("/updateGrocery")
	public String updateGrocery(@ModelAttribute("Grocery") Grocery g, HttpSession session) {
//		if(null != session.getAttribute("isAdmin") && (boolean)session.getAttribute("isAdmin") == true) {
			gRepo.update(g.getQuantity(),g.getName(),g.getCostPrice(),g.getSellPrice(),g.getId());
			return "redirect:/viewInventory";
//		}
//		return "redirect:/login";
	}
}