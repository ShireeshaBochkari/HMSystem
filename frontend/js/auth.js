// ---- login / register ----
function authView(mode){const L=mode==='login';
  $('#app').innerHTML=`<div class="card auth"><h2>🏠 Hostel Manager</h2><h3>${L?'Log in':'Create account'}</h3>
  <form id="f" novalidate><label>Username<input name="username" autocomplete="username"><small class="err" data-f="username"></small></label>
  <label>Password<input type="password" name="password" autocomplete="${L?'current-password':'new-password'}"><small class="err" data-f="password"></small></label>
  <small class="err" id="ferr"></small><button id="sb" style="width:100%">${L?'Log in':'Register'}</button></form>
  <p>${L?'New here? <a href="#/register">Register</a><br><small>Demo: admin/admin123 · student/student123</small>':'Have an account? <a href="#/login">Log in</a>'}</p></div>`;
  $('#f').onsubmit=async e=>{e.preventDefault();const d=Object.fromEntries(new FormData(e.target)),er={};
    if(d.username.trim().length<3)er.username='Min 3 characters';
    if(d.password.length<(L?1:6))er.password=L?'Required':'Min 6 characters';
    if(showErrors(er))return;const b=$('#sb');b.disabled=true;b.textContent='Please wait…';
    try{if(L){const r=await api('/auth/login',{method:'POST',body:JSON.stringify(d)});
        auth={token:r.token,user:r.username,role:r.role};localStorage.token=r.token;localStorage.user=r.username;localStorage.role=r.role;
        location.hash='#/students';route()}
      else{await api('/auth/register',{method:'POST',body:JSON.stringify(d)});toast('Registered. Please log in.');location.hash='#/login'}}
    catch(x){showErrors(x.errors||{},x.message);b.disabled=false;b.textContent=L?'Log in':'Register'}}}
