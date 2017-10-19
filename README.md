ArosIoT Platform LwM2M 
=============
ArgosIot Platform은 open Source Leshan(https://github.com/eclipse/leshan)을 기반으로 LwM2M Server와 LwM2M Client를 지원합니다. 
주요 기능과 특징은 다음과 같습니다.

- 언어 : JAVA
- Server : Tomcat 7.0 기반
- DB : Mysql, Redis (2.8.4), Mongo(3.x)
- Restful Open API : Lwm2M Object 및 Resource 를 수정할 수 있는 OPEN API를 지원합니다.
- 오픈 소스 : OMA Lightweight M2M server based on Leshan project (java inmplementation), MIT LIcense
- 주요 기능 : LwM2M Read,Write,Excute 등 operation 기능 지원, 펌웨어 업데이트 처리 등
- 라이 센스 : GNU General Public License : 해당 소스 코드는 GPL 라이선스로 모두 공개 되어 있으며 배포나 수정이 가능합니다.
단, 사용시 라이선스 및 저작권을 명시해야 합니다.



ArosIoT Platform LwM2M Server 사용
------
1. 해당 github (https://github.com/DKITechnology/LwM2M/)에서 `lwm2m-server` 프로젝트를 다운로드 받습니다.
2. 서버 관련 설정은 config 하위 폴더에 위치 하고 있으며 내용은 아래와 같습니다.
 * jdbc.properteis
 ``` java
# jdbc.default.* = Myslq 설정 관련 정보를 입력합니다.
# mongo.* = Mongo DB 설정 관련 정보를 입력합니다.
# redis.* = redis 설정 관련 정보를 입력합니다.
 ```
  * serverconfig.properteis
 ``` java
# lwm2m.server.* = L2M2M Server 설정 정보를 입력합니다.
ex) lwm2m.server.coapPort = LwM2M 서버 포트 (CoAP)
# lwm2m.default.format= 기본 메시지 형식 정보를 입력합니다.
ex) TLV, JSON, TEXT
#lwm2m.default.timeout=클라이언트로 부터 응답 대기 시간 정보를 입력합니다. (ms)
#device.noConn.min = 단말 n분 이상 Re-regisry 정보가 없는 경우 무응답을 처리합니다.
 ```
 #### LwM2M Server Test 가이드
 `lwm2m-server`
 
 
 
ArosIoT Platform LwM2M Client 사용
------

1. 해당 github (https://github.com/DKITechnology/LwM2M/)에서 `lwm2m-client` 프로젝트를 다운로드 받습니다.

