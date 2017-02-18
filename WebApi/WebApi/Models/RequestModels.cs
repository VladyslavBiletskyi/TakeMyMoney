using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace WebApi.Models
{
    class BuyTicketModel {
        [Required]
        public string UserId { set; get; }
        [Required]
        public string Token { set; get; }
        [Required]
        public int PerformanceId { get; set; }
    }
    public class UserInfoModel
    {
        public string UserId { get; set; }
        public string Email { get; set; }
    }
    public class TicketView
    {
        public Ticket Ticket { get; set; }
        public Performance Performance { get; set; }
    }
}