<p align="center">
<img src="https://user-images.githubusercontent.com/33706142/97104005-35c28c00-16eb-11eb-85e9-025d6926ade5.png" />
</p>

<h1 align="center">Kono</h1>
<p align="center">
    <em>💊 A simplified lightweight web framework.</em>
</p>

*When I used lots of out-of-box stuff, I wanna create something by myself, it does not matter whether it is fellowing the trending right now. I just wanna integrate those classic features from different languages/frameworks in my mind to one place, thus `Kono` is here, which is a simplified lightweight web framework.*



### ✨ Features

- Automatically route.

  It will automatically generate the route based on the controller and methods.

- Dynamical request support.

  You can use one method to support both `GET` and `POST` requests. 

- Flexible acquire parameters

  There is more flexible way to acquire request parameters, and support parameter preprocessing. 

- Streaming responses.

  Using the builder way to return your responses and build your own fully response.



### 📝 Usage

Let us have a look how it works easily. *(More details see the samples in project.)*

Create an `IndexController` supports the requests.

- GET /index/user
- POST /index/user

```java
// the default root route will be /index
public class IndexController extends BaseController {

    // both support the GET and POST request to /index/user
    public void user() {
        Optional<String> user = this.getRequest().get("user");
        Map<String, Object> map = new HashMap<>();
        map.put("Koy", "Hello World!");
        map.put(user.orElseGet(() -> "Anonymous"), "Kono!");
        this.getResponse().send(map).json();
    }
}
```



### 💡 Reference

Thanks to those projects which inspire me and give me references a lot. (and there will get more and more)

[SpringBoot](https://github.com/spring-projects/spring-boot) - A  popular, wonderful and elegant web framework in Java.

[Laravel](https://github.com/laravel/laravel) - A PHP framework for web artisans.

[ThinkPHP](https://github.com/top-think/framework) - A  easy to use PHP framework.

[Django](https://github.com/django/django) - The Web framework for perfectionists with deadlines in Python. 

[JOOQ](https://github.com/jOOQ/jOOQ) -  A fancy ORM lib to write SQL in Java.



### 📃 LICENSE

Apache-2.0 License [©Koy](https://github.com/Koooooo-7)

