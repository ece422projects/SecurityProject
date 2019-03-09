function submitLoginForm(){
  var uname = document.getElementById("loginUNAME").value;
  var psw = document.getElementById("loginPSW").value;
  var xhttp;
  xhttp=new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      processResponse(this);
    }
  };
  xhttp.open("POST", "/loginhandler", false);
  xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  xhttp.send("uname="+uname+"&psw="+psw);
}

function processResponse(xhttp){
  var response = xhttp.responseText;
  if(response=="Denied"){
    //notify user, incorrect username psw
    return;
  }
  window.location.replace(response);
}

function submitSignupForm(){
  var uname = document.getElementById("signupUNAME").value;
  var psw = document.getElementById("signupPSW").value;
  var xhttp;
  xhttp=new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      processResponse(this);
    }
  };
  xhttp.open("POST", "/signuphandler", false);
  xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  xhttp.send("uname="+uname+"&psw="+psw);
}
