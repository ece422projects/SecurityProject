var currentPath = "/Home";
var requestedPath = "/Home";

if(window.location.hash.substring(1)!=""){
  requestedPath = window.location.hash.substring(1)
  console.log("We recieve a hashed path: ");
}

function updateInfo(){
  //Just a wrapper for getting info and then updating UI
  console.log(requestedPath);
  var url = "/getInodes.t?path="+requestedPath;
  getInfo(url, updateContent);
  updateGroupInfo();
  getCorruptedFiles();
}

function showAlert(text) {
  var toast = document.getElementById("toast");
  toast.innerHTML = text;
  toast.className = "show";
  setTimeout(function(){ toast.className = toast.className.replace("show", ""); }, 3000);
}

function getCorruptedFiles(){
  var url = "/getCorruptedFiles";
  getInfo(url, updateAlertText);
}

function updateAlertText(xhttp){
  var files = xhttp.responseText;
  if(files!=null){
    if(localStorage.getItem("Alerted")==null){
      showAlert("The following files were corrupted "+files);
      localStorage.setItem("Alerted", true);
    }
  }
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

function updateGroupInfo(){
  var url = "/getGroups";
  getInfo(url, updateGroupList);
}

function updateGroupList(xhttp){
    var groupList = JSON.parse(xhttp.responseText);
    var groupDropdown = document.getElementById("groups");
    removeGroups(groupDropdown);
    var group;
    var i;
    for(i=0; i<groupList.length; i++){
      group = document.createElement('p');
      group.className = "groupLink";
      group.innerHTML = groupList[i];
      groupDropdown.appendChild(group);
    }
}

function removeGroups(dropdown){
  var groups = dropdown.querySelectorAll(".groupLink")
  var i;
  for(i=0; i<groups.length; i++){
    dropdown.removeChild(groups[i]);
  }
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
      var url = "/canViewFile?file="+currentPath+"/"+inodes[i].name+"#"+currentPath;
      itemDiv.addEventListener("dblclick", viewFileDbl(url));
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

function viewEditFile(url){
  console.log("Request", url);
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
        window.location = response;
      }
    };
    xhttp.open("GET", url, false);
    xhttp.send();
}


function createFileRequest(element){
  return function(){
    var xhttp;
    xhttp=new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
      if (this.readyState == 4 && this.status == 200) {
        getInfo("/getInodes.t?path="+currentPath, updateContent);
      }
    };
    console.log("we get in here");
    var url;
    var name;
    name = document.getElementById("newFileInput").value;
    url = "/newFile?inode="+currentPath+"/"+name;
    xhttp.open("GET", url, true);
    xhttp.send();
    document.getElementById("fileModal").style.display = "none";
  }
}

function newFolderRequest(element){
  return function(){
    var xhttp;
    xhttp=new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
      if (this.readyState == 4 && this.status == 200) {
        getInfo("/getInodes.t?path="+currentPath, updateContent);
      }
    };
    console.log("we get in here");
    var url;
    var name;
    name = document.getElementById("newFolderInput").value;
    url = "/newFolder?inode="+currentPath+"/"+name;
    xhttp.open("GET", url, true);
    xhttp.send();
    document.getElementById("folderModal").style.display = "none";
  }
}


var okNameFile = document.getElementById("newFileButton");
okNameFile.addEventListener("click",createFileRequest(okNameFile));

var okNameFolder = document.getElementById("NameFolderButton");
okNameFolder.addEventListener("click",newFolderRequest(okNameFolder));


function newGroupRequest(){
  var xhttp;
  xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function (){
    if(this.readyState == 4 && this.status == 200){
      //do things
      return;
    }
  }
  console.log("new group request");
  var grpName = document.getElementById("newGrpName").value;
  var users = document.getElementById("newGrpUsers").value;
  xhttp.open("POST", "/newGroup", true);
  xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  xhttp.send("grpName="+grpName+"&users="+users);
  document.getElementById("newGrpModal").style.display = "none";
  updateGroupInfo();
}

document.getElementById("okNewGrp").addEventListener("click", newGroupRequest);

// function editGroupRequest(){
//
// }
