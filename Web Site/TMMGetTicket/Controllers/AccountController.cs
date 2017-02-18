using Newtonsoft.Json;
using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading.Tasks;
using System.Web;
using System.Web.Mvc;
using TMMGetTicket.Models;

namespace TMMGetTicket.Controllers
{
    public class AccountController : Controller
    {
        string host = "http://takemymoneyapi.azurewebsites.net/";
        //string host = "http://localhost:2293/";
        static HttpClient CreateClient(string accessToken = "")
        {
            var client = new HttpClient();
            if (!string.IsNullOrWhiteSpace(accessToken))
            {
                client.DefaultRequestHeaders.Authorization =
                    new System.Net.Http.Headers.AuthenticationHeaderValue("Bearer", accessToken);
            }
            return client;
        }
        // GET: Account
        public ActionResult Cabinet()
        {
            if (HttpContext.Request.Cookies["token"] != null)
            {
                CabinetInfoModel model = new CabinetInfoModel();
                string responceString;
                using (var client = CreateClient(HttpContext.Request.Cookies["token"].Value))
                {
                    var response = client.GetAsync(host + "/api/Account/GetUser").Result;
                    responceString = response.Content.ReadAsStringAsync().Result;
                    model.User = JsonConvert.DeserializeObject<UserInfoModel>(responceString);
                    response = client.GetAsync(host + "/api/values/GetTickets").Result;
                    responceString = response.Content.ReadAsStringAsync().Result;
                    model.Tickets = JsonConvert.DeserializeObject<IEnumerable<Ticket>>(responceString).ToArray();
                }
                return View(model);
            }
            else
            {
                return RedirectToAction("Index", "Home", new { @result = "Ошибка покупки билета" });
            }
        }
        [HttpPost]
        public async Task<ActionResult> TicketDetail(int id)
        {
            if (HttpContext.Request.Cookies["token"] != null)
            {
                TicketViewModel ticket;
            var myContent = JsonConvert.SerializeObject(id);
            var buffer = System.Text.Encoding.UTF8.GetBytes(myContent);
            var byteContent = new ByteArrayContent(buffer);
            byteContent.Headers.ContentType = new MediaTypeHeaderValue("application/json");
            using (var client = CreateClient(HttpContext.Request.Cookies["token"].Value))
            {
                var response = client.PostAsync(host + "api/values/GetTicket", byteContent).Result;
                string ans = await response.Content.ReadAsStringAsync();
                ticket = JsonConvert.DeserializeObject<TicketViewModel>(ans);
            }
                if (ticket == null)
            {
                return HttpNotFound();
            }
            return PartialView(ticket);
            }
            return RedirectToAction("Index", "Home", new { @result = "Ошибка просмотра билета" });
        }
        public ActionResult Login()
        {
            return View();
        }
        [HttpPost]
        public ActionResult Login(LoginViewModel model)
        {
            var pairs = new List<KeyValuePair<string,string>>{
                new KeyValuePair<string,string>("grant_type","password"),
                new KeyValuePair<string, string>("username",model.Email),
                new KeyValuePair<string, string>("password",model.Password)
            };
            var requestData = new FormUrlEncodedContent(pairs);
            using (var client = new HttpClient())
            {
                var responce = client.PostAsync(host + "Token", requestData).Result;
                var result = responce.Content.ReadAsStringAsync().Result;
                var tokenDictionary = JsonConvert.DeserializeObject<Dictionary<string, string>>(result);
                if (tokenDictionary.ContainsKey("access_token"))
                {
                    HttpContext.Response.Cookies.Add(new HttpCookie("token",tokenDictionary["access_token"]));
                }
                else
                {
                    return RedirectToAction("Login", "Account");
                }
            }
            return RedirectToAction("Index", "Home",new { @result = "Вход успешен" });
        }
        public ActionResult Logout()
        {
            HttpContext.Response.Cookies.Remove("token");
            HttpCookie c = new HttpCookie("token");
            c.Expires = DateTime.Now.AddDays(-1);
            HttpContext.Response.Cookies.Add(c);
            return RedirectToAction("Index", "Home");
        }
        public ActionResult Register()
        {
            return View();
        }
        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<ActionResult> Register(RegisterViewModel model)
        {
            if (ModelState.IsValid)
            {
                HttpWebRequest request = (HttpWebRequest)WebRequest.Create(host + "api/Account/Register");
                request.Method = "POST";
                string requestString = JsonConvert.SerializeObject(model);
                request.ContentLength = requestString.Length;
                using (var writer = new StreamWriter(request.GetRequestStream()))
                {
                    await writer.WriteAsync(requestString);
                }
                WebResponse responce = request.GetResponse();
                if (((HttpWebResponse)responce).StatusCode == HttpStatusCode.OK)
                {
                    return RedirectToAction("Index", "Home", new { @result = "Регистрация успешно совершена" });
                }
                           }

            // Появление этого сообщения означает наличие ошибки; повторное отображение формы
            return View(model);
        }

    }
}