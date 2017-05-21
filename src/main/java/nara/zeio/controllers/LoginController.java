package nara.zeio.controllers;
import nara.zeio.authentication.PasswordAuthentication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import nara.zeio.authorization.Key;
import nara.zeio.authorization.AuthorizationResult;
import org.springframework.jdbc.core.RowMapper;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ModelAttribute;
import nara.zeio.authorization.AuthorizationDetails;

@RestController
@RequestMapping("/login")
class LoginController{

  @Autowired
  JdbcTemplate jdbcTemplate;

  @RequestMapping(method = RequestMethod.POST)
  AuthorizationResult isThereUser(@ModelAttribute Key key, HttpServletRequest request){
    PasswordAuthentication passwordAuthentication = new PasswordAuthentication();
    System.out.println(request.getSession().getAttribute("authResult"));
    List<Key> keys = jdbcTemplate.query( "select login, password from users", new RowMapper<Key>() {
      public Key mapRow(ResultSet rs, int rowNum) throws SQLException {
        Key key = new Key();
        key.setLogin(rs.getString("login"));
        key.setPassword(rs.getString("password"));
        return key;
      }
    });

    for (Key ikey : keys) if (passwordAuthentication.authenticate(key.getPassword().toCharArray(),ikey.getPassword()) && ikey.getLogin().equals(key.getLogin())){
      System.out.println("YOU MAY PASS");
      if (request.getSession().getAttribute("authResult") == null){
        request.getSession().setAttribute("authResult",new AuthorizationResult(true));
      } else {
        ((AuthorizationResult)request.getSession().getAttribute("authResult")).setResult(true);
      }

      //List<String> strList =
      List<String> strLst = jdbcTemplate.query("select email from users where login='"+ikey.getLogin()+"';", new RowMapper() {
        public String mapRow(ResultSet rs, int rowNum) throws SQLException{
          return rs.getString("email");
        }
      });

      System.out.println("email is "+strLst.get(0));
      request.getSession().setAttribute("authDeatails",new AuthorizationDetails(strLst.get(0), ikey.getLogin()));
      return new AuthorizationResult(true);
    }
    System.out.println("YOU SHALL NOT PASS");
    return new AuthorizationResult(false);
  }

  @RequestMapping(method = RequestMethod.GET)
  AuthorizationResult isThereUser(HttpServletRequest request){
    return (AuthorizationResult)request.getSession().getAttribute("authResult");
  }

  /*@RequestMapping(method = RequestMethod.POST)
  AuthorizationResult isThereUser( Key key){
    PasswordAuthentication passwordAuthentication = new PasswordAuthentication();

    List<Key> keys = jdbcTemplate.query( "select login, password from users", new RowMapper<Key>() {
      public Key mapRow(ResultSet rs, int rowNum) throws SQLException {
        Key key = new Key();
        key.setLogin(rs.getString("login"));
        key.setPassword(rs.getString("password"));
        return key;
      }
    });

    for (Key ikey : keys) if (passwordAuthentication.authenticate(key.getPassword().toCharArray(),ikey.getPassword()) && ikey.getLogin().equals(key.getLogin())){
      System.out.println("YOU MAY PASS");
      return new AuthorizationResult(true);
    }
    System.out.println("YOU SHALL NOT PASS");
    return new AuthorizationResult(false);
  }*/
}
