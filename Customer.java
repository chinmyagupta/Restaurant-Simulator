class Customer {
    Integer c_id;

    Integer q_index;
    int burger_required;
    Integer position_number;

    int in_time;
    Integer chef_time;
    int inside_time;

    Boolean before_chef;
    Boolean out_of_restaurant;

    Customer(int customer_id, int numb, int q_index, int t) {
        this.in_time = t;
        this.q_index = q_index;
        this.c_id = customer_id;
        before_chef = true;
        out_of_restaurant = false;
        this.burger_required = numb;
    }
}
