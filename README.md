IoT 통합 단말관리 서비스 플랫폼(ArgosIoT Platform)
=============
IoT 통합 단말관리 서비스 플랫폼(ArgosIoT Platform)은 국제 표준 규격인 LwM2M 기반의 저전력/저사양 IoT 단말 연동을 지원하는 IoT 통합관리 플랫폼 입니다. OMA에서 정의하는 최신 펌웨어 관리 및 업데이트 프로토콜을 지원하며, 타 시스템과 연동하기 위한 OPEN API를 제공합니다.

* 주요 특징 
 	* OMA LwM2M 프로토콜 지원 
 	* LwM2M 규격을 준수하는 펌웨어 업데이트 서비스 제공
 	* 동시 접속 수용을 위한 대용량 처리 모듈 제공
 	* Obect, Resource의 Read,Write,Excute 등 operation 기능 지원
 	* CoAP 프로토콜을 통한 펌웨어 패키지 다운로드
 	* Diff Package를 통한 IoT 단말 펌웨어 업데이트 기능 탑재
 	
	![Leshan](https://raw.githubusercontent.com/DKITechnology/LwM2M/DKITechnology-etc/concept_diagram.JPG)


Open Source을 기반으로 하는 LwM2M Server와 LwM2M Client를 지원합니다. 
주요 기능과 특징은 다음과 같습니다.

* 언어 : JAVA 1.7
* Server : Tomcat 7.x
* DB : Mysql, Redis (2.8.4), Mongo(3.x)
* Restful Open API : Lwm2M Object 및 Resource 를 관리 할 수 있는 OPEN API를 지원합니다.
* Open source : 해당 프로젝트는 해당 Open source 소스 라이선스 규정을 준수합니다.  
	* OMA Lightweight M2M server based on [Leshan project](https://github.com/eclipse/leshan)
	* Wakaama project 
* 라이 선스 : GNU General Public License. 해당 소스 코드는 GPL 라이선스로 모두 공개 되어 있으며 배포나 수정이 가능합니다. 단, 사용시 반드시 라이선스 및 저작권을 명시해야 합니다. 
<br/><br/><br/>

## Project

#### ArosIoT Platform LwM2M Server
------

> ArosIoT Platform LwM2M Server ( `lwm2m-server` )는 오픈소스 Leshan project 기반으로 생성되었습니다.<br/>
> 서버 관련 설정은 config 하위 폴더에 위치 하고 있으며 내용은 아래와 같습니다.
 * jdbc.properteis
 ``` java
# jdbc.default.* = Myslq DB 설정 관련 정보를 입력합니다.
# mongo.* = Mongo DB 설정 관련 정보를 입력합니다.
# redis.* = redis DB 설정 관련 정보를 입력합니다.
 ```
  * serverconfig.properteis
 ``` java
# lwm2m.server.* = L2M2M Server 설정 정보를 입력합니다.
ex) lwm2m.server.coapPort = LwM2M 서버 포트 (CoAP)
# lwm2m.default.format = 기본 메시지 형식 정보를 입력합니다.
ex) TLV, JSON, TEXT
#lwm2m.default.timeout =클라이언트로 부터 응답 대기 시간 정보를 입력합니다. (ms)
#device.noConn.min = 단말 n분 이상 Re-regisry 정보가 없는 경우 무응답을 처리합니다.
 ```
 
  * LwM2M Server Test 가이드
  ``` java
 1. 위의 설정을 참고하여 서버를 구동 시킵니다. (Tomcat 7.x)
 2. `lwm2m-testcase` 를 다운로드 받습니다.
 2. 구글 크롭 앱 스토어에서 Restlet Client를 설치합니다. (https://chrome.google.com/webstore/detail/restlet-client-rest-api-t/aejoelaoggembcahagimdiliamlcdmfm)
 3. 하단에 [import] 를 클릭한 후 다운로드 받은 `lwm2m-testcase`를 업로드 합니다.
 4. 구동시킨 서버 URI로 해당 어플리케이션을 실행합니다.
 ```
<br/> 
#### ArosIoT CoAP Download Server
------

> ArosIoT CoAP Downlad Server는 오픈소스 Californium (Cf) 기반으로 생성되었습니다.<br/> 
> DB에 저장된 펌웨어 조회 기능을 제공합니다.
> 조회된 펌웨어 패키지 정보는 `CoAP` 프로토콜을 통해 다운로드 할 수 있습니다.

<br/> 
#### ArosIoT Platform LwM2M Client
------

> ArosIoT Platform LwM2M Client ( `wakaama-dki` )는 오픈소스 wakaama 기반으로 생성되었습니다.<br/>
> 해당 프로젝트 주요 기능으로는 Blockwise를 이용한 CoAP 기반의 펌웨어 다운로드 기능과 펌웨어 업데이트 처리 기능 등이 있습니다.
 
