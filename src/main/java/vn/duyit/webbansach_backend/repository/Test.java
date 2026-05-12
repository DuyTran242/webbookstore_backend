package vn.duyit.webbansach_backend.repository;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import vn.duyit.webbansach_backend.repository.UserRepository;
@RestController
@RequestMapping("/test")
public class Test {

    @Autowired
    UserRepository userRepository;

    @GetMapping
    public String test(){
        return "Backend Running";
    }
}