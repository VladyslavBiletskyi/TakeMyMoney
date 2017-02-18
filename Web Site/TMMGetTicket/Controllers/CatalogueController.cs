using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Web;
using System.Web.Mvc;
using Newtonsoft.Json;
using System.Web.Routing;
using TMMGetTicket.Models;
using Newtonsoft.Json.Linq;
using static System.Net.WebRequestMethods;
using System.Collections.Specialized;
using System.IO;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading.Tasks;

namespace TMMGetTicket.Controllers
{
    public class CatalogueController : Controller
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
        // GET: Catalogue
        public ActionResult Index(int page=1)
        {
            ViewBag.Message = "Каталог";
            IEnumerable<Performance> performances;
            try
            {
                performances = JsonConvert.DeserializeObject<IEnumerable<Performance>>(new WebClient().DownloadString(host + "api/values/GetPerformances"));
            }
            catch
            {
                performances = new List<Performance>();
            }
            ViewBag.Page = page;
            return View(performances.ToList());
        }
        public ActionResult Detail(int id)
        {
            ViewBag.Message = "О мероприятии";
            IEnumerable<Performance> performances;
            try
            {
                performances = JsonConvert.DeserializeObject<IEnumerable<Performance>>(new WebClient().DownloadString(host + "api/values/GetPerformances"));
            }
            catch
            {
                performances = new List<Performance>();
            }
            return View(performances.Where(x=>x.Id==id).FirstOrDefault());
        }
        public ActionResult Buy(int id)
        {
            if (HttpContext.Request.Cookies["token"] != null)
            {
                var myContent = JsonConvert.SerializeObject(id);
                var buffer = System.Text.Encoding.UTF8.GetBytes(myContent);
                var byteContent = new ByteArrayContent(buffer);
                byteContent.Headers.ContentType = new MediaTypeHeaderValue("application/json");
                using (var client = CreateClient(HttpContext.Request.Cookies["token"].Value))
                {
                    var response = client.PostAsync(host + "api/values/BuyTicket", byteContent).Result;
                    if (response.IsSuccessStatusCode)
                    {
                        return RedirectToAction("Index", "Home", new { @result = "Покупка совершена" });
                    }
                }
            }
            return RedirectToAction("Index", "Home", new { @result = "Ошибка покупки билета" });
        }
        public async Task<ActionResult> ReturnTicket(int id)
        {
            if (HttpContext.Request.Cookies["token"] != null)
            {
                var myContent = JsonConvert.SerializeObject(id);
                var buffer = System.Text.Encoding.UTF8.GetBytes(myContent);
                var byteContent = new ByteArrayContent(buffer);
                byteContent.Headers.ContentType = new MediaTypeHeaderValue("application/json");
                using (var client = CreateClient(HttpContext.Request.Cookies["token"].Value))
                {
                    var response = client.PostAsync(host + "api/values/GetTicket", byteContent).Result;
                    string ans = await response.Content.ReadAsStringAsync();
                    if (ans != "")
                    {
                        TicketViewModel ticket = JsonConvert.DeserializeObject<TicketViewModel>(ans);
                        response = client.GetAsync(host + "/api/Account/GetUser").Result;
                        ans = response.Content.ReadAsStringAsync().Result;
                        if (ans == "")
                        {
                            return RedirectToAction("Index", "Home", new { @result = "Ошибка возврата билета" });
                        }
                        UserInfoModel user= JsonConvert.DeserializeObject<UserInfoModel>(ans);
                        if (ticket.Performance.BeginingDateTime>DateTime.Now.AddMinutes(30)&&
                            ticket.Ticket.UserId == user.UserId)
                        {
                            myContent = JsonConvert.SerializeObject(id);
                            buffer = System.Text.Encoding.UTF8.GetBytes(myContent);
                            byteContent = new ByteArrayContent(buffer);
                            response = client.PostAsync(host + "api/values/ReturnTicket", byteContent).Result;
                            if (response.IsSuccessStatusCode)
                            {
                                return RedirectToAction("Index", "Home", new { @result = "Билет успешно возвращен" });
                            }
                        }
                    }
                }
            }
            return RedirectToAction("Index", "Home", new { @result = "Ошибка возврата билета" });
        }
        public ActionResult PerformanceView(Performance perf)
        {
            return View();
        }
    }
}