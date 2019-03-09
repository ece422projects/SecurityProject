var currentPath = "/Home";
var requestedPath = "/Home";

if(window.location.hash.substring(1)!=""){
  requestedPath = window.location.hash.substring(1)
  console.log("We recieve a hashed path: ");
}

//Universal data request functions//

function getInfo(url, cFunction){
    var xhttp;
    xhttp=new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
      if (this.readyState == 4 && this.status == 200) {
        cFunction(this);
      }
    };
    xhttp.open("GET", url, true);
    xhttp.send();
}

function updateInfo(){
  //Just a wrapper for getting info and then updating UI
  console.log(requestedPath);
  var url = "/getInodes.t?path="+requestedPath;
  getInfo(url, updateContent);
}
function removeItems(container){
  var items = container.querySelectorAll(".item");
  var i;
  for(i=0; i<items.length; i++){
    container.removeChild(items[i]);
  }
}

function updateCurrentPath(){
  currentPath = requestedPath;
  document.getElementById("currentPath").innerHTML = currentPath;
}

function updateContent(xhttp){
  console.log("Response: " + xhttp.responseText);
  var content_container = document.getElementsByClassName("content_container")[0];
  removeItems(content_container);
  if(xhttp.responseText=="Denied"){

  }
  var inodes = JSON.parse(xhttp.responseText);

  if(inodes[0]=="Denied"){
    return; //could do a toast here, polish
  }
  updateCurrentPath();

  var itemDiv;
  var itemSpan1;
  var itemIcon;
  var itemSpan2;

  var i;
  for(i=0; i<inodes.length; i++){
    itemDiv = document.createElement('div');
    itemSpan1 = document.createElement('span');
    itemIcon = document.createElement('i');
    itemSpan2 = document.createElement('span');

    itemDiv.className = "item";
    itemSpan2.className = "label";
    itemSpan2.appendChild(document.createTextNode(inodes[i].name));

    if(inodes[i].type=="file"){
      itemSpan1.className = "file";
      itemIcon.className = "fas fa-file";

      itemSpan1.appendChild(itemIcon);
      itemDiv.appendChild(itemSpan1);
      itemDiv.appendChild(itemSpan2);
      itemDiv.addEventListener("dblclick", viewFile(inodes[i].name));
    }

    if(inodes[i].type=="folder"){
      itemSpan1.className = "folder";
      itemIcon.className = "fas fa-folder";

      itemSpan1.appendChild(itemIcon);
      itemDiv.appendChild(itemSpan1);
      itemDiv.appendChild(itemSpan2);
      itemDiv.addEventListener("dblclick", descendDir(inodes[i].name));
    }
    content_container.appendChild(itemDiv);
  }
}

//Element specific data request functions//
function descendDir(name){
  return function(){
    requestedPath = currentPath+"/"+name;
    updateInfo();
  }
}

function goBack(){
    if(currentPath!="/Home" && currentPath!="/Users" && currentPath!="/Documents"){
      console.log("we get here");
      requestedPath = currentPath.substr(0, currentPath.lastIndexOf("/"));
    }
    updateInfo();
}

document.getElementById("Backbtn").addEventListener("click", goBack);

function jumpTo(element){
  return function(){
    requestedPath = "/"+element.innerHTML;
    updateInfo();
  }
}

function assignDlinks(){
  var dLinks = document.getElementsByClassName("dLink");
  var i;
  for(i=0; i<dLinks.length; i++){
    dLinks[i].addEventListener("click", jumpTo(dLinks[i]));
  }
}

assignDlinks();
//To do make it so that the file is added in
function viewFile(filename){
  return function (){
    window.location.replace("/viewFile?file="+filename+"#"+currentPath);
  }
}

function newInodeRequest(element){
  return function(){
    var xhttp;
    xhttp=new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
      if (this.readyState == 4 && this.status == 200) {
        getInfo("/getInodes.t?path="+currentPath, updateContent);
      }
    };
    var url;
    var name;
    if(element.id=="okNameFile"){
      name = document.getElementById("newFileInput").value;
      url = "/newFile?inode="+currentPath+"/"+name;
    }
    else{
      name = document.getElementById("newFolderInput").value;
      url = "/newFolder?inode="+currentPath+"/"+name;
    }
    xhttp.open("GET", url, false);
    xhttp.send();
  }
}
