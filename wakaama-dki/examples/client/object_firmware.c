/*******************************************************************************
 *
 * Copyright (c) 2013, 2014 Intel Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * The Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Julien Vermillard - initial implementation
 *    Fabien Fleutot - Please refer to git log
 *    David Navarro, Intel Corporation - Please refer to git log
 *    Bosch Software Innovations GmbH - Please refer to git log
 *    Pascal Rieux - Please refer to git log
 *    
 *******************************************************************************/

/*
 * This object is single instance only, and provide firmware upgrade functionality.
 * Object ID is 5.
 */

/*
 * resources:
 * 0 package                          write
 * 1 package url                      write
 * 2 update                           exec
 * 3 state                            read
 * 4 update supported objects         read/write
 * 5 update result                    read
 * 6 package name                     read
 * 7 package version                  read
 * 8 firmware update protocol support read
 * 9 firmware update delivery method  read
 */

#include "liblwm2m.h"
#include "connection.h"
#include "internals.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <netdb.h>
#include <fcntl.h>
#include <unistd.h>
#include <errno.h>


// ---- private object "Firmware" specific defines ----
// Resource Id's:
#define RES_M_PACKAGE                   0
#define RES_M_PACKAGE_URI               1
#define RES_M_UPDATE                    2
#define RES_M_STATE                     3
#define RES_O_UPDATE_SUPPORTED_OBJECTS  4
#define RES_M_UPDATE_RESULT             5
#define RES_O_PKG_NAME                  6
#define RES_O_PKG_VERSION               7
#define RES_O_PROTOCOL_SUPPORT          8
#define RES_M_DELIVERY_METHOD           9

#define PRV_PROTOCOL_SUPPORT            2
#define PRV_DELIVERY_METHOD             0

#define LWM2M_DOWNLOAD_PORT_STR "5686"
#define LWM2M_DOWNLOAD_PORT      5686
#define MAX_PACKET_SIZE 1100
#define MAX_CHUNK_SIZE 1024


typedef struct
{
    uint8_t state;
    bool supported;
    uint8_t result;
    char * package_uri;
    char * package_name;
    char * package_version;
    char * package_file_name;
} firmware_data_t;


static uint8_t prv_firmware_read(uint16_t instanceId,
                                 int * numDataP,
                                 lwm2m_data_t ** dataArrayP,
                                 lwm2m_object_t * objectP)
{
    int i;
    uint8_t result;
    firmware_data_t * data = (firmware_data_t*)(objectP->userData);

    // this is a single instance object
    if (instanceId != 0)
    {
        return COAP_404_NOT_FOUND;
    }

    // is the server asking for the full object ?
    if (*numDataP == 0)
    {
        *dataArrayP = lwm2m_data_new(8);
        if (*dataArrayP == NULL) return COAP_500_INTERNAL_SERVER_ERROR;
        *numDataP = 8;
        (*dataArrayP)[0].id = 1;
        (*dataArrayP)[1].id = 3;
        (*dataArrayP)[2].id = 4;
        (*dataArrayP)[3].id = 5;
        (*dataArrayP)[4].id = 6;
        (*dataArrayP)[5].id = 7;
        (*dataArrayP)[6].id = 8;
        (*dataArrayP)[7].id = 9;
    }

    i = 0;
    do
    {
        switch ((*dataArrayP)[i].id)
        {
        case RES_M_PACKAGE:
            result = COAP_405_METHOD_NOT_ALLOWED;
            break;

        case RES_M_PACKAGE_URI:
       	 lwm2m_data_encode_string(data->package_uri, *dataArrayP + i);
       	 result = COAP_205_CONTENT;
           break;

        case RES_M_UPDATE:
            result = COAP_405_METHOD_NOT_ALLOWED;
            break;

        case RES_M_STATE:
            // firmware update state (int)
            lwm2m_data_encode_int(data->state, *dataArrayP + i);
            result = COAP_205_CONTENT;
            break;

        case RES_O_UPDATE_SUPPORTED_OBJECTS:
            lwm2m_data_encode_bool(data->supported, *dataArrayP + i);
            result = COAP_205_CONTENT;
            break;

        case RES_M_UPDATE_RESULT:
            lwm2m_data_encode_int(data->result, *dataArrayP + i);
            result = COAP_205_CONTENT;
            break;

        case RES_O_PKG_NAME:
        	 lwm2m_data_encode_string(data->package_name, *dataArrayP + i);
        	 result = COAP_205_CONTENT;
            break;

        case RES_O_PKG_VERSION:
            lwm2m_data_encode_string(data->package_version, *dataArrayP + i);
            result = COAP_205_CONTENT;
            break;

        case RES_O_PROTOCOL_SUPPORT:
            lwm2m_data_encode_int(PRV_PROTOCOL_SUPPORT, *dataArrayP + i);
            result = COAP_205_CONTENT;
            break;

        case RES_M_DELIVERY_METHOD:
            lwm2m_data_encode_int(PRV_DELIVERY_METHOD, *dataArrayP + i);
            result = COAP_205_CONTENT;
            break;

        default:
            result = COAP_404_NOT_FOUND;
        }

        i++;
    } while (i < *numDataP && result == COAP_205_CONTENT);

    return result;
}

static uint8_t prv_firmware_write(uint16_t instanceId,
                                  int numData,
                                  lwm2m_data_t * dataArray,
                                  lwm2m_object_t * objectP)
{
    int i;
    uint8_t result;
    firmware_data_t * data = (firmware_data_t*)(objectP->userData);

    // this is a single instance object
    if (instanceId != 0)
    {
        return COAP_404_NOT_FOUND;
    }

    i = 0;

    do
    {
        switch (dataArray[i].id)
        {
        case RES_M_PACKAGE:
            // inline firmware binary
            //result = COAP_204_CHANGED;
        	  // we don't support inline firmware binary
            result = COAP_405_METHOD_NOT_ALLOWED;
            break;

        case RES_M_PACKAGE_URI:
            // URL for download the firmware
        	 data->package_uri = lwm2m_malloc(dataArray[i].value.asBuffer.length+1);
            strncpy(data->package_uri, (char*)dataArray[i].value.asBuffer.buffer, dataArray[i].value.asBuffer.length);
            data->package_uri[dataArray[i].value.asBuffer.length] = 0;
            data->state = 1;
            result = COAP_204_CHANGED;
            break;

        case RES_O_UPDATE_SUPPORTED_OBJECTS:
            if (lwm2m_data_decode_bool(&dataArray[i], &data->supported) == 1)
            {
                result = COAP_204_CHANGED;
            }
            else
            {
                result = COAP_400_BAD_REQUEST;
            }
            break;

        default:
            result = COAP_405_METHOD_NOT_ALLOWED;
        }

        i++;
    } while (i < numData && result == COAP_204_CHANGED);

    return result;
}

static uint8_t prv_firmware_execute(uint16_t instanceId,
                                    uint16_t resourceId,
                                    uint8_t * buffer,
                                    int length,
                                    lwm2m_object_t * objectP)
{
    firmware_data_t * data = (firmware_data_t*)(objectP->userData);

    // this is a single instance object
    if (instanceId != 0)
    {
        return COAP_404_NOT_FOUND;
    }

    if (length != 0) return COAP_400_BAD_REQUEST;

    // for execute callback, resId is always set.
    switch (resourceId)
    {
    case RES_M_UPDATE:
        if (data->state == 2)
        {
            fprintf(stdout, "\n\t FIRMWARE UPDATE\r\n\n");
            // trigger your firmware download and update logic
    		data->state = 3;
          return COAP_204_CHANGED;
        }
        else
        {
            // firmware update already running
            return COAP_400_BAD_REQUEST;
        }
    default:
        return COAP_405_METHOD_NOT_ALLOWED;
    }
}

char * get_package_name()
{
#ifdef TARGET_OS_LINUX
    FILE *fp;
    char buffer[256];

    /* Open the command for reading. */
    fp = popen("/bin/uname -s", "r");
    if (fp == NULL) {
        fprintf(stdout, "Failed to run command\n" );
        return NULL;
    }

    /* Read the output a line at a time - output it. */
    if (fgets(buffer, sizeof(buffer)-1, fp) == NULL) {
	     pclose(fp);
        return NULL;
    }

    /* close */
    pclose(fp);

    return lwm2m_strdup(buffer);
#else

#endif
}

char * get_package_version()
{
#ifdef TARGET_OS_LINUX
    FILE *fp;
    char buffer[256];

    /* Open the command for reading. */
    fp = popen("/bin/uname -r", "r");
    if (fp == NULL) {
        fprintf(stdout, "Failed to run command\n" );
        return NULL;
    }

    /* Read the output a line at a time - output it. */
    if (fgets(buffer, sizeof(buffer)-1, fp) == NULL) {
	     pclose(fp);
        return NULL;
    }

    /* close */
    pclose(fp);

    return lwm2m_strdup(buffer);
#else

#endif
}

#define TEXT_BUFFSIZE 1024

void get_host(char* src, char* host, char* uri, int* port)
{
    char* pA;
    char* pB;
    char* pC;
    char port_str[6];
    int i = 0, len = 0;

    memset(host, 0, sizeof(host));
    memset(uri, 0, sizeof(uri));
    memset(port_str, 0, 6);
    *port = 0;
    if (!(*src)){
        return;
    }

    pA = src;
    if (!strncmp(pA, "http://", strlen("http://"))){
        pA = src + strlen("http://");
    }else if (!strncmp(pA, "https://", strlen( "https://"))){
        pA = src + strlen( "https://");
    }else if (!strncmp(pA, "coap://", strlen( "coap://"))){
        pA = src + strlen( "coap://");
    }else if (!strncmp(pA, "coaps://", strlen( "coaps://"))){
        pA = src + strlen( "coaps://");
    }

    pB = strchr(pA, '/');
    pC = strchr(pA, ':');
    LOG_ARG("pA = %s", pA);
    LOG_ARG("pB = %s", pB);
    LOG_ARG("pC = %s", pC);

    if(pC) {
    	memcpy(port_str, pC+1, strlen(pC-1) - strlen(pB));
    	*port = atoi(port_str);
    } else {
    	*port = 80;
    }

    if (pB){
    	if(pC) {
    		memcpy(host, pA, strlen(pA) - strlen(pC));
    	} else {
    		memcpy(host, pA, strlen(pA) - strlen(pB));
    	}
        if (pB + 1){
            memcpy(uri, pB, strlen(pB));
            uri[strlen(pB)] = 0;
        }
    }else{
    	if(pC) {
    		memcpy(host, pA, strlen(pA) - strlen(pC));
    	} else {
          memcpy(host, pA, strlen(pA));
    	}
    }

//    if (pB){
//    	host[strlen(pA) - strlen(pB)] = 0;
//    }else{
//    	host[strlen(pA)] = 0;
//    }
//
//    if (pC){
//        *port = atoi(pC + 1);
//        *pA = 0;
//    }else{
//        *port = 80;
//    }
}

int download_file_by_http(char *url, char * fileName)
{
    int sockfd = -1;
    char buffer[2049] = "";
    struct sockaddr_in   server_addr;
    struct hostent   *host;
    int port = 0;
    int nbytes = 0;
    char host_addr[256] = "";
    char host_file[1024] = "";
    FILE *fp;
    char request[1024] = "";
    int i = 0;
	int  running = 1;
	int ret;
	int is_read_header = 0;
	char * bufp;

    LOG_ARG( "download url : %s", url);
    get_host(url, host_addr, host_file, &port);
    LOG_ARG( "download host:%s, port:%d, uri:%s", host_addr, port, host_file);

    if ((host = gethostbyname(host_addr)) == NULL)
    {
        fprintf(stderr, "gethostbyname error \n ");
        return -1;
    }

    if ((sockfd = socket(AF_INET, SOCK_STREAM, 0)) == -1)
    {
        fprintf(stderr, "socket error\n ");
        return -1;
    }

    bzero(&server_addr, sizeof(server_addr));
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(port);
    server_addr.sin_addr = *((struct in_addr*)host->h_addr);

    LOG_ARG("start connect to %s...", host_addr);

    if (connect(sockfd, (struct sockaddr*)(&server_addr), sizeof(struct sockaddr)) == -1)
    {
        fprintf(stderr, "connect error\n ");
        return -1;
    }

    sprintf(request, "GET %s HTTP/1.1\r\nHost:%s\r\nConnection: Keep-Alive\r\n\r\n", host_file, host_addr);

    LOG_ARG( "%s\n", request);

	if (send(sockfd, request, strlen(request) + 1, 0) == -1) {
		fprintf(stderr,  "send error!\n ");
       return -1;
	}

	LOG( "\nThe following is the response header:\n ");

    fp = fopen(fileName, "wb");
    if(fp == NULL) {
    	fprintf(stderr,  "file open error!\n ");
    	return -1;
    }

    while (running) {
		memset(buffer, 0, sizeof(buffer));
		int bufflen = recv(sockfd, buffer, TEXT_BUFFSIZE, 0);

		if (bufflen < 0) {
			LOG("socket was closed.");
		}

		if (bufflen > 0) {
			if(is_read_header == 1) {
			    fwrite(buffer, sizeof(char), bufflen , fp);
			} else {
              // skip header ..
				bufp = strstr(buffer, "\r\n\r\n");
				if(bufp == NULL) {
					LOG_ARG("%s", buffer);
				} else {
				    is_read_header = 1;
				    fwrite(bufp+4, sizeof(char), bufflen - (bufp-buffer+4) , fp);
				}
			}
		} else {
			running = 0;
		}
	}

    fclose(fp);
    close(sockfd);
    return 0;
}


void display_firmware_object(lwm2m_object_t * object)
{
#ifdef WITH_LOGS
    firmware_data_t * data = (firmware_data_t *)object->userData;
    fprintf(stdout, "  /%u: Firmware object:\r\n", object->objID);
    if (NULL != data)
    {
        fprintf(stdout, "    state: %u, supported: %s, result: %u\r\n",
                data->state, data->supported?"true":"false", data->result);
    }
#endif
}

lwm2m_object_t * get_object_firmware(char * fileName)
{
    /*
     * The get_object_firmware function create the object itself and return a pointer to the structure that represent it.
     */
    lwm2m_object_t * firmwareObj;

    firmwareObj = (lwm2m_object_t *)lwm2m_malloc(sizeof(lwm2m_object_t));

    if (NULL != firmwareObj)
    {
        memset(firmwareObj, 0, sizeof(lwm2m_object_t));

        /*
         * It assigns its unique ID
         * The 5 is the standard ID for the optional object "Object firmware".
         */
        firmwareObj->objID = LWM2M_FIRMWARE_UPDATE_OBJECT_ID;

        /*
         * and its unique instance
         *
         */
        firmwareObj->instanceList = (lwm2m_list_t *)lwm2m_malloc(sizeof(lwm2m_list_t));
        if (NULL != firmwareObj->instanceList)
        {
            memset(firmwareObj->instanceList, 0, sizeof(lwm2m_list_t));
        }
        else
        {
            lwm2m_free(firmwareObj);
            return NULL;
        }

        /*
         * And the private function that will access the object.
         * Those function will be called when a read/write/execute query is made by the server. In fact the library don't need to
         * know the resources of the object, only the server does.
         */
        firmwareObj->readFunc    = prv_firmware_read;
        firmwareObj->writeFunc   = prv_firmware_write;
        firmwareObj->executeFunc = prv_firmware_execute;
        firmwareObj->userData    = lwm2m_malloc(sizeof(firmware_data_t));

        /*
         * Also some user data can be stored in the object with a private structure containing the needed variables
         */
        if (NULL != firmwareObj->userData)
        {
            ((firmware_data_t*)firmwareObj->userData)->state = 0;
            ((firmware_data_t*)firmwareObj->userData)->supported = false;
            ((firmware_data_t*)firmwareObj->userData)->result = 0;
            ((firmware_data_t*)firmwareObj->userData)->package_uri = NULL;
            ((firmware_data_t*)firmwareObj->userData)->package_name = get_package_name();
            ((firmware_data_t*)firmwareObj->userData)->package_version = get_package_version();
            ((firmware_data_t*)firmwareObj->userData)->package_file_name = fileName;
        }
        else
        {
            lwm2m_free(firmwareObj);
            firmwareObj = NULL;
        }
    }

    return firmwareObj;
}

void free_object_firmware(lwm2m_object_t * objectP)
{
    if (NULL != objectP->userData)
    {
    	 lwm2m_free(((firmware_data_t*)objectP->userData)->package_uri);
    	 lwm2m_free(((firmware_data_t*)objectP->userData)->package_name);
    	 lwm2m_free(((firmware_data_t*)objectP->userData)->package_version);
        lwm2m_free(objectP->userData);
        objectP->userData = NULL;
    }
    if (NULL != objectP->instanceList)
    {
        lwm2m_free(objectP->instanceList);
        objectP->instanceList = NULL;
    }
    lwm2m_free(objectP);
}
connection_t * dl_connection_new_incoming(connection_t * connList,
                                       int sock,
                                       struct sockaddr * addr,
                                       size_t addrLen)
{
    connection_t * connP;

    connP = (connection_t *)malloc(sizeof(connection_t));
    if (connP != NULL)
    {
        connP->sock = sock;
        memcpy(&(connP->addr), addr, addrLen);
        connP->addrLen = addrLen;
        connP->next = connList;
    }

    return connP;
}

connection_t * dl_connection_create(connection_t * connList,
                                 int sock,
                                 char * host,
                                 char * port,
                                 int addressFamily)
{
    struct addrinfo hints;
    struct addrinfo *servinfo = NULL;
    struct addrinfo *p;
    int s;
    struct sockaddr *sa;
    socklen_t sl;
    connection_t * connP = NULL;

    memset(&hints, 0, sizeof(hints));
    hints.ai_family = addressFamily;
    hints.ai_socktype = SOCK_DGRAM;

    if (0 != getaddrinfo(host, port, &hints, &servinfo) || servinfo == NULL) return NULL;

    // we test the various addresses
    s = -1;
    for(p = servinfo ; p != NULL && s == -1 ; p = p->ai_next)
    {
        s = socket(p->ai_family, p->ai_socktype, p->ai_protocol);
        if (s >= 0)
        {
            sa = p->ai_addr;
            sl = p->ai_addrlen;
            if (-1 == connect(s, p->ai_addr, p->ai_addrlen))
            {
                close(s);
                s = -1;
            }
        }
    }
    if (s >= 0)
    {
        connP = dl_connection_new_incoming(connList, sock, sa, sl);
        close(s);
    }
    if (NULL != servinfo) {
        free(servinfo);
    }

    return connP;
}

int dl_create_socket(const char * portStr, int addressFamily)
{
    int s = -1;
    struct addrinfo hints;
    struct addrinfo *res;
    struct addrinfo *p;

    memset(&hints, 0, sizeof hints);
    hints.ai_family = addressFamily;
    hints.ai_socktype = SOCK_DGRAM;
    hints.ai_flags = AI_PASSIVE;

    if (0 != getaddrinfo(NULL, portStr, &hints, &res))
    {
        return -1;
    }

    for(p = res ; p != NULL && s == -1 ; p = p->ai_next)
    {
        s = socket(p->ai_family, p->ai_socktype, p->ai_protocol);
        if (s >= 0)
        {
            if (-1 == bind(s, p->ai_addr, p->ai_addrlen))
            {
                close(s);
                s = -1;
            }
        }
    }

    freeaddrinfo(res);

    return s;
}

int dl_connection_send(connection_t *connP,
                    uint8_t * buffer,
                    size_t length)
{
    int nbSent;
    size_t offset;

#ifdef WITH_LOGS
    char s[INET6_ADDRSTRLEN];
    in_port_t port;

    s[0] = 0;

    if (AF_INET == connP->addr.sin6_family)
    {
        struct sockaddr_in *saddr = (struct sockaddr_in *)&connP->addr;
        inet_ntop(saddr->sin_family, &saddr->sin_addr, s, INET6_ADDRSTRLEN);
        port = saddr->sin_port;
    }
    else if (AF_INET6 == connP->addr.sin6_family)
    {
        struct sockaddr_in6 *saddr = (struct sockaddr_in6 *)&connP->addr;
        inet_ntop(saddr->sin6_family, &saddr->sin6_addr, s, INET6_ADDRSTRLEN);
        port = saddr->sin6_port;
    }

    fprintf(stderr, "Sending %d bytes to [%s]:%hu\r\n", length, s, ntohs(port));

    output_buffer(stderr, buffer, length, 0);
#endif

    offset = 0;
    while (offset != length)
    {
        nbSent = sendto(connP->sock, buffer + offset, length - offset, 0, (struct sockaddr *)&(connP->addr), connP->addrLen);
        if (nbSent == -1) return -1;
        offset += nbSent;
    }
    return 0;
}

int dl_transaction_send(lwm2m_context_t * contextP,
                     lwm2m_transaction_t * transacP)
{
    bool maxRetriesReached = false;

    LOG("Entering dl_transaction_send");
    if (transacP->buffer == NULL)
    {
        transacP->buffer_len = coap_serialize_get_size(transacP->message);
        if (transacP->buffer_len == 0) return COAP_500_INTERNAL_SERVER_ERROR;

        transacP->buffer = (uint8_t*)lwm2m_malloc(transacP->buffer_len);
        if (transacP->buffer == NULL) return COAP_500_INTERNAL_SERVER_ERROR;

        transacP->buffer_len = coap_serialize_message(transacP->message, transacP->buffer);
        if (transacP->buffer_len == 0)
        {
            lwm2m_free(transacP->buffer);
            transacP->buffer = NULL;
            transaction_remove(contextP, transacP);
            return COAP_500_INTERNAL_SERVER_ERROR;
        }
    }

    if (!transacP->ack_received)
    {
        long unsigned timeout;

        if (0 == transacP->retrans_counter)
        {
            time_t tv_sec = lwm2m_gettime();
            if (0 <= tv_sec)
            {
                transacP->retrans_time = tv_sec + COAP_RESPONSE_TIMEOUT;
                transacP->retrans_counter = 1;
                timeout = 0;
            }
            else
            {
                maxRetriesReached = true;
            }
        }
        else
        {
            timeout = COAP_RESPONSE_TIMEOUT << (transacP->retrans_counter - 1);
        }

        if (COAP_MAX_RETRANSMIT + 1 >= transacP->retrans_counter)
        {
            connection_t * connP = (connection_t*) transacP->peerH;
            if (connP == NULL)
            {
                fprintf(stderr, "#> failed sending %lu bytes, missing connection\r\n", transacP->buffer_len);
                //return COAP_500_INTERNAL_SERVER_ERROR ;
                return -1;
            }

            if (-1 == dl_connection_send(connP, transacP->buffer, transacP->buffer_len))
            {
                fprintf(stderr, "#> failed sending %lu bytes\r\n", transacP->buffer_len);
                //return COAP_500_INTERNAL_SERVER_ERROR ;
                return -1;
            }

            transacP->retrans_time += timeout;
            transacP->retrans_counter += 1;
        }
        else
        {
            maxRetriesReached = true;
        }
    }

    if (transacP->ack_received || maxRetriesReached)
    {
        transaction_remove(contextP, transacP);
        return -1;
    }

    return 0;
}


// start dowonloading a firmware file by coap
int download_file_by_coap(char *url, char * fileName)
{
	lwm2m_context_t * contextDL = NULL;
	int sock = 0;
	int port = 0;
	int nbytes = 0;
	uint8_t buffer[MAX_PACKET_SIZE];
	int i = 0;
	char host_addr[16] = "";
	char host_file[100] = "";
	char port_str[6] = "";
	connection_t * newConnP = NULL;
	const char * serverPort = LWM2M_DOWNLOAD_PORT_STR;
	struct timeval tv;
	uint32_t block_num = 0;
	uint32_t expected_block_num = 0;
	uint16_t block_size = MAX_CHUNK_SIZE;
	uint32_t block_offset = 0;
	uint8_t block_more = 0;
	int64_t new_offset = 0;
	lwm2m_transaction_t * transaction = NULL;
	int result = 0;
    FILE *fp;


	LOG_ARG( "download url : %s", url);
	get_host(url, host_addr, host_file, &port);
	LOG_ARG( "download host:%s, port:%d, uri:%s", host_addr, port, host_file);

	contextDL = lwm2m_init(NULL);

	sprintf(port_str, "%d", port);
	sock = dl_create_socket(serverPort, AF_INET);

	//set ack waiting time as 2 seconds
	tv.tv_sec = COAP_RESPONSE_TIMEOUT;
	tv.tv_usec = 0;
	setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, (const char*) &tv, sizeof(struct timeval));
	newConnP = dl_connection_create(NULL, sock, host_addr, port_str, AF_INET);

	if (newConnP != NULL)
	{
		LOG("Download server connection opened");
	    fp = fopen(fileName, "wb");
	    if(fp == NULL) {
	    	fprintf(stderr,  "file open error!\n ");
	    	return -1;
	    }

		transaction = transaction_new(newConnP, COAP_GET, NULL, NULL, contextDL->nextMID++, 4, NULL);
		if (transaction == NULL) {
			return -1;
        }
		coap_set_header_uri_path(transaction->message, host_file);

		while(true) {
		   if (dl_transaction_send(contextDL, transaction) != 0) {
			   LOG("Requesting firmware binary is failed !!");
			   result = -1;
			   break;
		   }

		   struct sockaddr_storage addr;
		   socklen_t addrLen;
		   addrLen = sizeof(addr);

		   // We retrieve the data received
		   nbytes = recvfrom(sock, buffer, MAX_PACKET_SIZE, 0, (struct sockaddr *)&addr, &addrLen);
		   if (0 > nbytes) {
			   fprintf(stderr, "Error in recvfrom(): %d %s\r\n", errno, strerror(errno));
			   result = -1;
			   break;
		   } else if(0 == nbytes) {
			   // need to retransmit packect
			   continue;
		   }

		   coap_status_t coap_error_code = NO_ERROR;
		   static coap_packet_t message[1];
		   static coap_packet_t response[1];

		   coap_error_code = coap_parse_message(message, buffer, (uint16_t)nbytes);
		   if (coap_error_code == NO_ERROR) {
			   LOG_ARG("Parsed: ver %u, type %u, tkl %u, code %u.%.2u, mid %u, Content type: %d", message->version, message->type, message->token_len, message->code >> 5, message->code & 0x1F, message->mid, message->content_type);
			   LOG_ARG("Payload: %.*s", message->payload_len, message->payload);
			   if (message->code >= COAP_GET && message->code <= COAP_DELETE) {
				   // this packet is unexpected
					LOG("Unexpected packtet is received");
				   result = -1;
				   break;
			   } else {
				   // get offset for blockwise transfers
				   if (coap_get_header_block2(message, &block_num, &block_more, &block_size, &block_offset)) {
					   LOG_ARG("Blockwise: block request %u/%u (%u/%u) @ %u bytes, more %d", block_num, expected_block_num, block_size, MAX_CHUNK_SIZE, block_offset, block_more);
					   if(expected_block_num == block_num) {
						   if(message->code == COAP_205_CONTENT) {
							   // save payload to file
							   fwrite(message->payload, sizeof(char), message->payload_len , fp);
							   if(block_more) {
								   expected_block_num = ++block_num;
							   } else {
								   result = 0;
								   break;
							   }
						   }
					   }

					   transaction_free(transaction);
					   transaction = transaction_new(newConnP, COAP_GET, NULL, NULL, contextDL->nextMID++, message->token_len, message->token);
					   if (transaction == NULL) {
						   return -1;
					   }
					   coap_set_header_uri_path(transaction->message, host_file);
					   block_size = MIN(block_size, MAX_CHUNK_SIZE);
					   //set heaher block2
					   coap_set_header_block2(transaction->message, block_num, 0, block_size);
				   } else {
					   if(message->code == COAP_205_CONTENT) {
						   fwrite(message->payload, sizeof(char), message->payload_len , fp);
						   result = 0;
						   break;
					   }
				   }
			   }
		   } else {
				LOG_ARG("coap_error_code = %d", coap_error_code);
			   result = -1;
			   break;
		   }
        }
	} else {
		LOG("Connecting Download server failed");
	   result = -1;
    }

	// need to close socket and free memory
   transaction_free(transaction);
   close(sock);
   fclose(fp);

   return result;
}

void check_and_update_firmware(lwm2m_object_t * objectP)
{
	firmware_data_t * fwData = (firmware_data_t*)objectP->userData;
	switch(fwData->state)
	{
	case 1:
       LOG("Start downloading firmware");
		if (!strncmp(fwData->package_uri, "http", strlen("http"))){
			if(download_file_by_http(fwData->package_uri, fwData->package_file_name) > -1)
			{
				fwData->state = 2;
			} else {
				fwData->result = 8;
				fwData->state = 0;
			}
		}else {
			if(download_file_by_coap(fwData->package_uri, fwData->package_file_name) > -1)
			{
				fwData->state = 2;
			} else {
				fwData->result = 8;
				fwData->state = 0;
			}
		}
	    break;
	case 2:
	    break;
	case 3:
		// call update agent
		fwData->state = 0;
		fwData->result = 1;
	    break;
	default:
		break;
	}
}

