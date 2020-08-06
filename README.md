# Shiro jCasbin Spring Boot Starter

## Shiro integrates Casbin to replace Shiro's own authorization role verification

# English | [中文](./README_CN.md)

## Reference
Currently not published to maven, if you want to use it anyway, please clone this repository and run `mvn package` locally.

## Getting Started
1. Download [model_request.conf](./src/main/resources/conf/model_request.conf) put into `classpath:casbin/model_request.conf`
or write your own model_request.conf to customize the model. If you choose to customize the model, please refer to the [configuration](#Configuration) section to specify the path of the model file in the configuration file.
2. Just add the `@RequiresCasbin` annotation to the controller method, and the request will verify the request address permissions.
Fill in the three parameters of Enforcer's enforce method here, which are: Shiro's `principals` value, requested path address `ServletPath`, requested method type `method`
    
        @RequireCasbin
        @GetMapping("/index")
        public String index() {
            return "index";
        }
3. Just add the `@HasRoleForUser(role = "arole")` annotation to the controller method, and the request will verify that the user has the specified permissions.
 The two elements here are: Shiro's `principals` value, string constant passed in annotation `role`
 
        @HasRoleForUser(role = "role1")
        @PostMapping("/update")
        @ResponseBody
        public String update() {
            ...
        }

4. Assigning permissions to the request interface
    
        CasbinSubject subject = (CasbinSubject) SecurityUtils.getSubject();
        Enforcer enforcer = subject.getEnforcer(); // get enforcer
    
        String path = "/menu/root";
        String method = "GET";
        enforcer.addPermissionForUser(role, path, method); // Of course, according to the principle of rbac, the first parameter can be role or user
        enforcer.addRoleForUser(user, role); // Assign role to user
    

## Configuration
The following values ​​are default values

    shiro-jcasbin:
      enabled: true //Enabled jcasbin
      rule-table: casbin_rule_request //policy table name
      model: classpath:casbin/model_request.conf //madel path
      watcher: true //Enabled watcher，Need jetcd support
      watcher-key: /casbin/watcher_request //Watcher's key name
      synced: false //disable read-write locks(thread unsafe)
