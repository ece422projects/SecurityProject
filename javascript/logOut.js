function logOut(){
  window.location.replace("/logOut");
}
document.getElementById("logOut").addEventListener("click", logOut);

function viewFileDbl(url){
  console.log("Request", url);
  return function(){
    var xhttp;
    xhttp=new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
      if (this.readyState == 4 && this.status == 200) {
        var response = this.responseText;
        if(response=="Denied"){
          console.log("We get a denied");
          return;
        }
        console.log(response);
        window.location.replace(response);
      }
    };
    xhttp.open("GET", url, false);
    xhttp.send();
    localStorage.setItem("Alerted",null);
  }
}
