http-proxy
==========
  Aggregate multiple webservice endpoint  
  Loadbalancing  
  Authentication - Basic(TBD)  
  IP Filtering (TBD)  
  Logging + Metric  
  Monitoring  

## Implemention and requirement
  Netty and NIO  
  No storage in sql. Only depends on cassandra on runtime.  
  Simple configuration(yaml).  Configuration should be able to refresh without restart  

## Closing of sockets
  Request and endpoint determine the closing of socket, if neither endpoint are close, the connection will stay open for a period of 60 second.

## Aggregate multiple webservice endpoint
  As as frontend to different webservice.  This does not modify http parameter, body.  It will just passthru to servers behind in the configuration.  There will be default server and

## Loadbalancing
  Simple round robin to server that are alive.  HTTP ping server to determine if they are alive.  Server that does not return http 200 within 2 consecutive ping will be marked as down.

## Logging
  By default all message are log and metric are send to graphite.  
  Specific url pattern can be specific to disable logging.  

## Metric
Metric can be enable. Currently using graphite metric
```
#eg. 25ms get request to catalog1 catalog1.get_request_200 25
```  
## Heath Monitoring
Local accessible port to do the following:  
- connections open per endpoint, url  
- request handled per endpoint, url  
- list of endpoint(alive, down)  + the count of request


## Sample Configuration
```
##Default for route
route_default:	
	logging: req, req_body(POST,PUT), resp_body(POST)
	metric: "{server_name}.{verb}_{route_name}_{response_code}"

##Default for server
server_default:
	balancer: rr
	health: /health

##servers:
##	- name: name of this list of server
##	  target: list of server
##	  health: health url.  use 'ping' to fall back to tcp ping. 'off' to disable 
##	  balancer: load balancing algorithm. RR(round robin), LU(least use)
##routes:	
##	- name: name of this route
##	  url: {verb} {regex of url}
##	  server: can be defined server or just ip
##	  metric: the string of graphite metric.  variable can be used 
##			  server_name: name of server
##			  server_target: the selected server
##			  verb: the verb of this request
##			  route_name: name of the route
##			  response_code: the http status code in the response
##	  logging: off, req, req_header, req_body, resp_header, resp_body
##		       can also max in verb, so req(POST,PUT) or req(*) == req

## Configuration begin here
servers:
	- name: did_server
	  target: 192.168.93.100:8080,192.168.93.101:8080
	- name: pos_server
	  target: 192.168.93.100:9090,192.168.93.101:9090
      health: /myhealth
      balancer: LU
	- name: catalog
	  target: 192.168.93.100:9090/build_123,192.168.93.101:9090/build_123
      health: ping
      balancer: LU      
    - name: default_server
      target: 192.168.93.102:8080

routes:	
	- name: did
	  url: "* /didv2/dids.*"
	  server: did_server
	  logging: 'off'

	- name: new_order
	  url: "POST /pos/.+/orders/.+"
	  server: pos_server
	  metric: "{server_name}.{route_name}_{response_code}"

#match /product, /products, /sku, skus
	- name: catalog
	  url: "* /(product(s|)|sku(s|))/.*"
	  server: catalog_server #balancer, health is default from server_default

#map all url to old server
#logging all req, req
#target can also point directly to an ip instead of server
	- name: unmapped
	  url: "* .*"
	  server: default_server
	  logging: req      
```

## How to use
- Prerequisite
  - Install rabitmq(3.3.5) on running machine (http://www.rabbitmq.com/install-debian.html)  	
  - Should increase maximum file open limit (ulimit) 
    Open /etc/security/limits.conf, add following lines:  
      username     soft    nofile          65535  
      username     hard    nofile          65535  
- App config  (/src/main/resource/app.properties)  
```
DB.HOST = cassandra_host
DB.USERNAME = 
DB.PASS = 
DB.DBNAME = http_proxy

METRIC.HOST = metric_host
METRIC.PORT = metric_port

HTTP_PROXY.NUM_THREAD = 1000
HTTP_PROXY.PORT = proxy_port
HTTP_PROXY.ADMIN_PORT = proxy_admin_port
HTTP_PROXY.CONFIG_NAME = configuration.yaml
```
- Proxy config :/src/main/resource/configuration.yaml  
  Should have admin config :  
```   
servers:  
    - name: dummy_server  
      target: 127.0.0.1:9999 #9999: admin port  
      health : 'off'  

   routes: 
    - name: dummy_checkhealth  
      url: "GET /http_proxy/.*"  
      server: dummy_server  
      logging: 'off'  
      metric: 'off'  
```
- Reload config  
  http://localhost:admin_port/http_proxy/reloadconfig   
  http://proxyhost:proxy_port/http_proxy/reloadconfig  
  (header must have Username:admin, Password:admin properties)
- Url to see the health of endpoint  
  http://localhost:admin_port/http_proxy/monitor  
  http://proxyhost:proxy_port/http_proxy/monitor 
- Get source  
  $ git clone https://github.com/lunextelecom/http-proxy.git
- Install  
   mvn clean install -DskipTests=true
- Run: java -jar -Xms2500m -Xmx2500m target/http-proxy-1.0-SNAPSHOT.jar 
- (optional)Config haproxy(1.4.24)  
  Health check for http_proxy: option httpchk HEAD /http_proxy/checkhealth HTTP/1.0  
  Exam: 
```
  listen webcluster *:9000  
       mode    http  
       balance roundrobin  
       option httpchk HEAD /http_proxy/checkhealth HTTP/1.0  
       option forwardfor  
       option httpclose
       server web01 10.9.9.111:8080 check 
       server web02 10.9.9.112:8080 check 
```
  
  
