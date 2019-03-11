var currentPath = window.location.hash.substring(1);
console.log("Current Path: "+currentPath);
var filename = window.location.search;
filename = filename.replace("?file=","");
console.log("Filename: "+filename);

function saveFile(){
  //Do post here
  var text = document.getElementById("textEditor").value;
  var xhttp;
  xhttp=new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      goBack();
    }
  };
  xhttp.open("POST", "/saveFile?file="+filename, true);
  xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  xhttp.send("body="+text);
}

try{
  document.getElementById("saveFile").addEventListener("click", saveFile);
}
catch{
  //
}

function goBack(){
  window.location.replace("/home.html#"+currentPath);
}
document.getElementById("Backbtn").addEventListener("click", goBack);
