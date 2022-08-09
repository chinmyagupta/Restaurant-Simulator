
class BurgerOrder {
    int c_id;

    int q_index;
    int chef_time;
    int out_time;

    int c_position;
    int count;

    BurgerOrder(int id, int queue, int b, int customerLocation) {
        c_id = id;
        out_time = 0;
        count = b;
        c_position = customerLocation;
        q_index = queue;

    }
}