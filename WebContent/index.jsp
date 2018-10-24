<%--
  Created by IntelliJ IDEA.
  User: zzk08
  Date: 2018/10/9
  Time: 22:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String username = (String) session.getAttribute("username");
    if (username == null) {
        response.sendRedirect("account.jsp");
    }
%>
<html>
    <head>
        <title>Home</title>
        <style>
            body {
                background-color: black;
            }

            #centered-box {
                height: 670px;
                width: 68%;
                margin: 30px auto;
                background-color: #778899;
                border: 1px solid whitesmoke;
            }

            #centered-box > #left-full-height {
                margin: 0;
                padding: 0;
                height: 100%;
                width: 20%;
                background-color: dodgerblue;
                float: left;
            }

            #centered-box > #right-full-height {
                margin: 0;
                padding: 0;
                height: 100%;
                width: 80%;
                background-color: greenyellow;
                float: left;
            }

            #right-up-is-8 {
                height: 75%;
                background: darkgrey;
            }

            #right-up-is-8 > ul#received {
                height: 100%;
                padding-left: 20px;
                padding-right: 20px;
                margin: 0;
                overflow-y: scroll;
            }

            #right-up-is-8 > ul#received > li {
                list-style: none;
                border-radius: 5px;
                padding: 5px;
                margin: 3px;
                clear: both;
            }

            #right-up-is-8 > ul#received > li[class] {
                background-color: #122b40;
                color: white;
            }

            #right-up-is-8 > ul#received > li.others {
                float: left;
            }

            #right-up-is-8 > ul#received > li.me {
                float: right;
            }

            #right-lower-is-2 {
                height: 25%;
                background-color: white;
            }

            #right-lower-is-2 > #message-zone {
                height: 80%;
            }

            #right-lower-is-2 > #message-zone textarea {
                margin: 2px;
                padding: 2px;
                height: 88%;
                width: 98%;
                font-size: 28px;
                resize: none;
                color: cornflowerblue;
                border: none;
                border-radius: 5px;
                overflow: paged-y-controls;
            }

            #right-lower-is-2 > #message-zone textarea:hover, #right-lower-is-2 > #message-zone textarea:focus {
                border: 1px solid dodgerblue;
            }

            #right-lower-is-2 > #bottom-btn-group {
                height: 20%;
                width: 100%;
            }

            #right-lower-is-2 > #bottom-btn-group button {
                float: right;
                margin: auto 10px;
                padding: 5px 30px;
                border: 1px solid dodgerblue;
                border-radius: 5px;
                color: dodgerblue;
                background-color: white;
            }

            #right-lower-is-2 > #bottom-btn-group button:hover {
                background-color: #d2d2d2;
            }

            #right-lower-is-2 > #bottom-btn-group button:active {
                margin-top: 2px;
            }

            ul#left-items {
                clear: both;
            }

            ul#left-items > li {
                color: black;
                list-style-type: none;
            }
        </style>
    </head>
    <body>
        <div id="centered-box">
            <div id="left-full-height">
                <ul id="left-items">

                </ul>
            </div>
            <div id="right-full-height">
                <div id="right-up-is-8">
                    <ul id="received">
                    </ul>
                </div>
                <div id="right-lower-is-2">
                    <div id="message-zone">
                        <label for="msg"></label>
                        <textarea name="msg" id="msg"></textarea>
                    </div>
                    <div id="bottom-btn-group">
                        <button id="send">发送</button>
                    </div>
                </div>
            </div>
        </div>
    </body>
    <script src="assets/wp.main.js"></script>
    <script src="assets/wp.air.js"></script>
    <script src="assets/wp.message.js"></script>
</html>