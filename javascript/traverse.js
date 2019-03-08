
// function traverse(element){
//   return function(){
//     if(element.className=="item"){
//       currentPath += item.querySelector('.label').innerHTML;
//       console.log(currentPath);
//     }
//     if(element.className=="dLink"){
//       currentPath = element.innerHTML + "/";
//     }
//     if(element.id=="Backbtn"){
//       if(!currentPath=="Home/" || !currentPath=="Users/" || !currentPath=="Documents/"){
//         currentPath = currentPath.substr(0, currentPath.lastIndexOf("/")+1);
//       }
//     }
//     updateInfo();
//   }
// }

// function assignTraverse(){
//   var dLinks = document.getElementsByClassName("dLink");
//   var i;
//   for(i=0; i<dLinks.length; i++){
//     dLinks[i].addEventListener("click", traverse(dLinks[i]));
//   }
//   var inodes = document.getElementsByClassName("item");
//   for(i=0; i<inodes.length; i++){
//     if(inodes[i].querySelector("span").className=="folder"){
//       inodes[i].addEventListener("click", traverse(inodes[i]));
//     }
//   }
// }
